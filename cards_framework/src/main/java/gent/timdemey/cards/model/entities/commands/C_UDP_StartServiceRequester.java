package gent.timdemey.cards.model.entities.commands;


import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.netcode.UDP_ServiceRequester;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.context.LimitedContext;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.ISerializationService;

/**
 * Command sent over UDP broadcast to the network in order to detect servers.
 * 
 * @author Timmos
 *
 */
public class C_UDP_StartServiceRequester extends CommandBase
{
    public C_UDP_StartServiceRequester()
    {
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
        CommandBase clrServList = new C_ClearServerList();
        IContextService contextServ = Services.get(IContextService.class);
        contextServ.getContext(ContextType.UI).schedule(clrServList);

        // prepare UDP broadcast
        C_UDP_Request cmd = new C_UDP_Request();
        
        CommandDtoMapper dtoMapper = Services.get(ISerializationService.class).getCommandDtoMapper();
        String json = dtoMapper.toJson(cmd);
        
        int udpport = state.getConfiguration().getServerUdpPort();
        
        UDP_ServiceRequester udpServRequester = new UDP_ServiceRequester(json, udpport, C_UDP_StartServiceRequester::onUdpReceived);
        state.setUdpServiceRequester(udpServRequester);

        udpServRequester.start();
    }

    public static void onUdpReceived(String json)
    {
        try
        {
            CommandDtoMapper dtoMapper = Services.get(ISerializationService.class).getCommandDtoMapper();
            CommandBase command = dtoMapper.toCommand(json);
            if (!(command instanceof C_UDP_Response))
            {
                Logger.warn("Unexpected command on UDP datagram, class: " + command.getClass().getSimpleName());
                return;
            }

            IContextService contextServ = Services.get(IContextService.class);
            LimitedContext context = contextServ.getContext(ContextType.UI);
            context.schedule(command);
        }
        catch (Exception ex)
        {
            Logger.error("Failed to deserialize UDP datagram, ignoring", ex);
            return;
        }
    }

    @Override
    public String toDebugString()
    {
        return "";
    }
}
