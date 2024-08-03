package gent.timdemey.cards.model.entities.commands;


import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_UDP_StartServiceRequester;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.model.net.NetworkFactory;
import gent.timdemey.cards.model.net.UDP_ServiceRequester;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import java.util.UUID;
import gent.timdemey.cards.di.IContainerService;

/**
 * Command sent over UDP broadcast to the network in order to detect servers.
 * 
 * @author Timmos
 *
 */
public class C_UDP_StartServiceRequester extends CommandBase
{
    private final CommandFactory _CommandFactory;
    private final CommandDtoMapper _CommandDtoMapper;
    private final Logger _Logger;
    private final NetworkFactory _NetworkFactory;
    
    public C_UDP_StartServiceRequester(
        Container container, CommandFactory commandFactory, NetworkFactory networkFactory, CommandDtoMapper commandDtoMapper, Logger logger, 
        P_UDP_StartServiceRequester parameters)
    {
        super(container, parameters);
        
        this._CommandFactory = commandFactory;
        this._NetworkFactory = networkFactory;
        this._CommandDtoMapper = commandDtoMapper;
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
        C_UDP_Request cmd = _CommandFactory.CreateUDPRequest();
        
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
            if (!(command instanceof C_UDP_Response))
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
