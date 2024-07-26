package gent.timdemey.cards.model.entities.commands;


import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.model.net.NetworkFactory;
import gent.timdemey.cards.model.net.UDP_ServiceRequester;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.context.LimitedContext;
import gent.timdemey.cards.services.interfaces.IContextService;
import java.util.UUID;

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
    
    C_UDP_StartServiceRequester(
        IContextService contextService, CommandFactory commandFactory, NetworkFactory networkFactory, CommandDtoMapper commandDtoMapper, Logger logger,
        UUID id)
    {
        super(contextService, id);
        
        this._CommandFactory = commandFactory;
        this._NetworkFactory = networkFactory;
        this._CommandDtoMapper = commandDtoMapper;
        this._Logger = logger;
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);
        return CanExecuteResponse.yes();
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);

        if (state.getUdpServiceRequester() != null)
        {
            throw new IllegalStateException("Already a requesting service running. Stop the current one first.");
        }

        // clear the server list shown in the UI
        C_ClearServerList clrServList = _CommandFactory.CreateClearServerList();
        _ContextService.getContext(ContextType.UI).schedule(clrServList);

        // prepare UDP broadcast
        C_UDP_Request cmd = _CommandFactory.CreateUDPRequest();
        
        String json = _CommandDtoMapper.toJson(cmd);
        
        int udpport = state.getConfiguration().getServerUdpPort();
        
        UDP_ServiceRequester udpServRequester = _NetworkFactory.createUDPServiceRequester(json, udpport, this::onUdpReceived);
        state.setUdpServiceRequester(udpServRequester);

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

            LimitedContext context = _ContextService.getContext(ContextType.UI);
            context.schedule(command);
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
