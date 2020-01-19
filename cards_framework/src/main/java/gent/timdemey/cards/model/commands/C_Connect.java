package gent.timdemey.cards.model.commands;

import java.net.InetAddress;
import java.util.UUID;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.multiplayer.io.CommandSchedulingTcpConnectionListener;
import gent.timdemey.cards.multiplayer.io.ITcpConnectionListener;
import gent.timdemey.cards.multiplayer.io.TCP_Connection;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionCreator;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionPool;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.context.LimitedContext;

/**
 * Let a player connect to an online game.
 * 
 * @author Tim
 *
 */
public class C_Connect extends CommandBase
{
    final InetAddress srvInetAddress;
    final int tcpport;
    final String playerName;

    public C_Connect(InetAddress srvInetAddress, int tcpport, String playerName)
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
        }
        else if (type == ContextType.Client)
        {
            state.setLocalName(playerName);
            ICardPlugin plugin = Services.get(ICardPlugin.class);

            if (plugin.getPlayerCount() == 1)
            {
                throw new UnsupportedOperationException();
            }         

            ITcpConnectionListener tcpConnListener = new ClientCommandSchedulingTcpConnectionListener();
            TCP_ConnectionPool tcpConnPool = new TCP_ConnectionPool(1, tcpConnListener);

            // state.setTcpConnectionCreator(tcpConnectionCreator);
            state.setTcpConnectionPool(tcpConnPool);
            state.setTcpConnectionListener(tcpConnListener);

            TCP_ConnectionCreator.connect(tcpConnPool, srvInetAddress, tcpport);
        }
        else
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

        @Override
        public void onTcpConnectionRemotelyClosed(UUID id, TCP_Connection connection)
        {
            LimitedContext context = Services.get(IContextService.class).getContext(ContextType.Client);
            if (id != null)
            {
                CommandBase cmd = new C_HandleConnectionLoss(connection, id);
                context.schedule(cmd);
            }
        }
    }
}
