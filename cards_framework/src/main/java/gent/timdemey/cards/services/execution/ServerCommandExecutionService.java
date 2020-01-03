package gent.timdemey.cards.services.execution;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.contract.ICommand;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.model.commands.HelloClientCommand;
import gent.timdemey.cards.model.commands.HelloServerCommand;
import gent.timdemey.cards.model.commands.C_Composite;
import gent.timdemey.cards.model.commands.C_CreateServer;
import gent.timdemey.cards.model.commands.C_DenyClient;
import gent.timdemey.cards.model.commands.C_DropPlayer;
import gent.timdemey.cards.model.commands.C_Move;
import gent.timdemey.cards.model.commands.CommandEnvelope;
import gent.timdemey.cards.multiplayer.io.ITcpConnectionListener;
import gent.timdemey.cards.multiplayer.io.TCP_Connection;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionAccepter;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionPool;
import gent.timdemey.cards.multiplayer.io.UDP_ServiceAnnouncer;
import gent.timdemey.cards.serialization.Json;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.state.ContextLimited;

public class ServerCommandExecutionService extends CommandExecutionServiceBase
{

	TCP_ConnectionPool srv_tcp_connpool = null;
	private TCP_ConnectionAccepter srv_tcp_accepter = null;

	public ServerCommandExecutionService()
	{
		super(ContextType.Server);
	}

	@Override
	protected String getThreadName()
	{
		return THREAD_NAME;
	}

	@Override
	protected void execute(CommandEnvelope envelope)
	{
		ICommand command = envelope.command;
		Services.get(ILogManager.class).log("Processing command '" + command.getClass().getSimpleName() + "'");
		command.execute();

		Context context = Services.get(IContextService.class).getThreadContext();
		// UUID localId = context.getLocalId();
		if (command instanceof C_Move || command instanceof C_Composite)
		{
			List<UUID> idsToSend = context.getPlayerIdsExcept(envelope.getMetaInfo().requestingParty);
			srv_tcp_connpool.broadcast(idsToSend, envelope.serialize());
		}

	}

	

	private class ConnListener implements ITcpConnectionListener
	{

		@Override
		public void onTcpMessageReceived(TCP_Connection connection, String message)
		{
			try
			{
				CommandEnvelope envelope = Json.receive(message);
				ICommand cmd = envelope.command;
				cmd.setVolatileData(connection);

				Services.get(ILogManager.class).log(
				        "Received command '" + cmd.getClass().getSimpleName() + "' from " + connection.getRemote());
				envelope.reschedule(ContextType.Server);
			} catch (Exception e)
			{
				Services.get(ILogManager.class).log(e);
			}
		}

		@Override
		public void onTcpConnectionAdded(TCP_Connection connection)
		{
			Services.get(ILogManager.class).log("TCP connection added to " + connection.getRemote());

			int requiredPlayerCount = Services.get(ICardPlugin.class).getPlayerCount();

		}

		@Override
		public void onTcpConnectionLocallyClosed(TCP_Connection connection, UUID id)
		{

		}

		@Override
		public void onTcpConnectionRemotelyClosed(TCP_Connection connection, UUID id)
		{
			if (id != null)
			{
				ContextLimited context = Services.get(IContextService.class).getContext(ContextType.Server);
				ICommand cmd = new C_DropPlayer(id);
				context.commandProcessor.schedule(cmd);
			}
		}

	}

	
	void stopServer()
	{
		if (srv_udp_announcer != null)
		{
			srv_udp_announcer.stop();
		}
		if (srv_tcp_accepter != null)
		{
			srv_tcp_accepter.stop();
		}
	}
}
