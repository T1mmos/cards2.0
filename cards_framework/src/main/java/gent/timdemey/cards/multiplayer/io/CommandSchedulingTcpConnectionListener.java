package gent.timdemey.cards.multiplayer.io;

import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.ILogManager;
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
	public void onTcpMessageReceived(UUID id, TCP_Connection tcpConnection, String message)
	{
		try
		{
			CommandBase command = CommandDtoMapper.toCommand(message);
			
			// attach metadata to the command
			command.setSourceTcpConnection(tcpConnection);
			command.setSourceId(id);
			command.setSerialized(message);
			
			Services.get(ILogManager.class).log("Received command '" + command.getClass().getSimpleName() + "' from " + tcpConnection.getRemote());
			
			LimitedContext context = Services.get(IContextService.class).getContext(contextType);
			context.schedule(command);
		} catch (Exception e)
		{
			Services.get(ILogManager.class).log(e);
		}
	}


	@Override
	public void onTcpConnectionLocallyClosed(UUID id, TCP_Connection connection)
	{
		
	}

	@Override
	public void onTcpConnectionRemotelyClosed(UUID id, TCP_Connection connection)
	{
		
	}
}
