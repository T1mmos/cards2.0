package gent.timdemey.cards.model.commands;

import java.net.InetAddress;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.multiplayer.io.CommandSchedulingTcpConnectionListener;
import gent.timdemey.cards.multiplayer.io.ITcpConnectionListener;
import gent.timdemey.cards.multiplayer.io.TCP_Connection;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionCreator;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionPool;
import gent.timdemey.cards.services.ICommandExecutionService;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.context.LimitedContext;
import gent.timdemey.cards.services.execution.ClientCommandExecutionService;

public class C_Connect extends CommandBase
{
	final InetAddress srvInetAddress;
	final int tcpport;
	final String playerName;

	C_Connect(InetAddress srvInetAddress, int tcpport, String playerName)
	{
		this.srvInetAddress = srvInetAddress;
		this.tcpport = tcpport;
		this.playerName = playerName;
	}

	@Override
	public void execute(Context context, ContextType type, State state)
	{
		if (type == ContextType.UI)
		{
			state.setLocalName(playerName);

			reschedule(ContextType.Client);
		} else if (type == ContextType.Client)
		{
			state.setLocalName(playerName);
			ICardPlugin plugin = Services.get(ICardPlugin.class);

			if (plugin.getPlayerCount() == 1)
			{
				throw new UnsupportedOperationException();
			}

			ICommandExecutionService cmdExecServ = new ClientCommandExecutionService();
			Services.get(IContextService.class).initialize(ContextType.Client, cmdExecServ);

			ITcpConnectionListener tcpConnListener = new ClientCommandSchedulingTcpConnectionListener();			
			TCP_ConnectionPool tcpConnPool = new TCP_ConnectionPool(1, tcpConnListener);

			// state.setTcpConnectionCreator(tcpConnectionCreator);
			state.setTcpConnectionPool(tcpConnPool);
			state.setTcpConnectionListener(tcpConnListener);

			TCP_ConnectionCreator.connect(tcpConnPool, srvInetAddress, tcpport);
		} else
		{
			throw new IllegalStateException();
		}
	}

	@Override
	protected boolean canExecute(Context context, ContextType type, State state)
	{
		return true;
	}

	@Override
	protected void undo(Context context, ContextType type, State state)
	{

	}

	private static class ClientCommandSchedulingTcpConnectionListener extends CommandSchedulingTcpConnectionListener
	{
		private ClientCommandSchedulingTcpConnectionListener()
		{
			super(ContextType.Client);
		}

		@Override
		public void onTcpConnectionAdded(TCP_Connection connection)
		{
			super.onTcpConnectionAdded(connection);

			// if the connection is complete then we can join the game
			CommandBase cmd = new C_JoinGame();
			IContextService contextServ = Services.get(IContextService.class);
			LimitedContext context = contextServ.getContext(ContextType.Client);
			context.schedule(cmd);
		}
	}
}
