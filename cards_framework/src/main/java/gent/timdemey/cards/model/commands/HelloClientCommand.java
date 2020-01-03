package gent.timdemey.cards.model.commands;

import java.net.InetAddress;
import java.util.UUID;

import gent.timdemey.cards.model.other.Server;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

/**
 * Command sent over UDP to a client in order to let it know that a server is
 * running on the network. It contains initial info needed by a client to
 * connect, if possible,g to the running game on the server.
 * 
 * @author Timmos
 *
 */
public final class HelloClientCommand extends CommandBase
{
	public final String serverName;
	public final InetAddress inetAddress;
	public final int tcpport;
	public final int majorVersion;
	public final int minorVersion;

	public HelloClientCommand(String serverName, InetAddress inetAddress, int tcpport, int majorVersion, int minorVersion)
	{
		this(UUID.randomUUID(), serverName, inetAddress, tcpport, majorVersion, minorVersion);
	}
	
	public HelloClientCommand(UUID id, String serverName, InetAddress inetAddress, int tcpport, int majorVersion, int minorVersion)
	{
		super(id);
		
		this.serverName = serverName;
		this.inetAddress = inetAddress;
		this.tcpport = tcpport;
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
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
			reschedule(ContextType.UI);
		} 
		else if (type == ContextType.UI)
		{
			Server server = new Server(serverName, inetAddress, tcpport);
			state.addServer(server);
		} 
		else
		{
			// this command is sent over the wire by UDP_ServiceAnnouncer 
		}
	}
}
