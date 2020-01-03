package gent.timdemey.cards.model.commands;

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
public class HelloServerCommand extends CommandBase
{

	public HelloServerCommand()
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
			HelloServerCommand cmd = new HelloServerCommand();
			String json = CommandDtoMapper.toJson(cmd);
			

			UDP_ServiceRequester udpServRequester = new UDP_ServiceRequester(json, HelloServerCommand::onUdpReceived);
			state.setUdpServiceRequester(udpServRequester);
			
			udpServRequester.start();
		} 
		else
		{
			throw new IllegalStateException("Not meant to run in a processor in context " + type);
		}
	}
	
	public static void onUdpReceived (String json)
	{		
        try 
        {
            CommandBase command = CommandDtoMapper.toCommand(json);
            if (!(command instanceof HelloClientCommand))
            {
                Services.get(ILogManager.class).log("Unexpected command on UDP datagram, class: " + command.getClass().getSimpleName());
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
