package gent.timdemey.cards.model.entities.commands.net;


import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.dialogs.C_ClearServerList;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CommandFactory;
import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;
import gent.timdemey.cards.model.net.NetworkFactory;
import gent.timdemey.cards.model.net.UDP_ServiceRequester;
import gent.timdemey.cards.services.context.ContextType;

/**
 * Command starting an UDP_ServiceRequester service, to broadcast an UDP info request message
 * in order to receive UDP info response messages from any available server in the network.
 * @author Timmos
 */
public class C_UDP_StartServerInfoRequestService extends CommandBase<P_UDP_StartServerInfoRequestService>
{
    private final CommandFactory _CommandFactory;
    private final Logger _Logger;
    private final NetworkFactory _NetworkFactory;
    
    public C_UDP_StartServerInfoRequestService(
        Container container, CommandFactory commandFactory, NetworkFactory networkFactory, Logger logger, 
        P_UDP_StartServerInfoRequestService parameters)
    {
        super(container, parameters);
        
        this._CommandFactory = commandFactory;
        this._NetworkFactory = networkFactory;
        this._Logger = logger;
    }

    @Override
    public CanExecuteResponse canExecute()
    {
        CheckContext(ContextType.UI);
        return CanExecuteResponse.yes();
    }

    @Override
    public void execute()
    {
        CheckContext(ContextType.UI);

        if (_State.getUdpServiceRequester() != null)
        {
            throw new IllegalStateException("Already a requesting service running. Stop the current one first.");
        }

        // clear the server list shown in the UI
        C_ClearServerList clrServList = _CommandFactory.CreateClearServerList();
        schedule(ContextType.UI, clrServList);

        // prepare UDP broadcast
        C_UDP_GetServerInfoRequest cmd = _CommandFactory.CreateUDPGetServerInfoRequest();
        String json = _CommandDtoMapper.toJson(cmd);
        
        int udpport = _State.getConfiguration().getServerUdpPort();
        
        UDP_ServiceRequester udpServRequester = _NetworkFactory.createUDPServiceRequester(json, udpport, this::onUdpReceived);
        _State.setUdpServiceRequester(udpServRequester);

        udpServRequester.start();
    }

    public void onUdpReceived(String json)
    {
        try
        {
            CommandBase command = _CommandDtoMapper.toCommand(json);
            if (!(command instanceof C_UDP_GetServerInfoResponse))
            {
                _Logger.warn("Unexpected command on UDP datagram, class: " + command.getClass().getSimpleName());
                return;
            }

            _CommandExecutor.schedule(command);
        }
        catch (Exception ex)
        {
            _Logger.error("Failed to deserialize UDP datagram, ignoring", ex);
            return;
        }
    }

    @Override
    public String toDebugString()
    {
        return "";
    }
}
