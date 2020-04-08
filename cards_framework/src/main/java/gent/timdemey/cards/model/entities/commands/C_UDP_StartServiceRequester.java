package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.multiplayer.io.UDP_ServiceRequester;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.context.LimitedContext;

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
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return true;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        CheckNotContext(type, ContextType.Server);

        if (type == ContextType.UI)
        {
            reschedule(ContextType.Client);
            return;
        }

        if (type == ContextType.Client)
        {
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
            String json = getCommandDtoMapper().toJson(cmd);

            UDP_ServiceRequester udpServRequester = new UDP_ServiceRequester(json, C_UDP_StartServiceRequester::onUdpReceived);
            state.setUdpServiceRequester(udpServRequester);

            udpServRequester.start();
        }
    }

    public static void onUdpReceived(String json)
    {
        try
        {
            CommandBase command = getCommandDtoMapper().toCommand(json);
            if (!(command instanceof C_UDP_Response))
            {
                Services.get(ILogManager.class)
                        .log("Unexpected command on UDP datagram, class: " + command.getClass().getSimpleName());
                return;
            }

            IContextService contextServ = Services.get(IContextService.class);
            LimitedContext context = contextServ.getContext(ContextType.Client);
            context.schedule(command);
        }
        catch (Exception e)
        {
            Services.get(ILogManager.class).log("Failed to deserialize UDP datagram, ignoring");
            return;
        }
    }
}
