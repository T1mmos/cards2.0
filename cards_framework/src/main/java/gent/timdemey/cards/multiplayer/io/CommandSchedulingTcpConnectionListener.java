package gent.timdemey.cards.multiplayer.io;

import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.model.commands.C_HandleConnectionLoss;
import gent.timdemey.cards.model.commands.CommandBase;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.context.LimitedContext;

public class CommandSchedulingTcpConnectionListener implements ITcpConnectionListener
{
	private final ContextType contextType;
	
	public CommandSchedulingTcpConnectionListener (ContextType contextType)
	{
		this.contextType = contextType;
	}
	
	@Override
	public void onTcpConnectionAdded(TCP_Connection connection)
	{
		Services.get(ILogManager.class).log("A TCP connection was added to " + connection.getRemote());
	}
	
	@Override
	public void onTcpMessageReceived(TCP_Connection tcpConnection, String message)
	{
		try
		{
			CommandBase command = CommandDtoMapper.toCommand(message);
			
			// attach the tcp connection on which the command was received
			command.setSourceTcpConnection(tcpConnection);
			
			Services.get(ILogManager.class).log("Received command '" + command.getClass().getSimpleName() + "' from " + tcpConnection.getRemote());
			
			LimitedContext context = Services.get(IContextService.class).getContext(contextType);
			context.schedule(command);
		} catch (Exception e)
		{
			Services.get(ILogManager.class).log(e);
		}
	}


	@Override
	public void onTcpConnectionLocallyClosed(TCP_Connection connection, UUID id)
	{
		
	}

	@Override
	public void onTcpConnectionRemotelyClosed(TCP_Connection connection, UUID id)
	{
		LimitedContext context = Services.get(IContextService.class).getContext(contextType);
		if (id != null)
		{			
			CommandBase cmd = new C_HandleConnectionLoss(connection, id);
			context.schedule(cmd);
		}
	}
}
