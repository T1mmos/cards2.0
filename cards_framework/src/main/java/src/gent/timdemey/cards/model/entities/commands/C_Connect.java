package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.multiplayer.io.CommandSchedulingTcpConnectionListener;
import gent.timdemey.cards.multiplayer.io.ITcpConnectionListener;
import gent.timdemey.cards.multiplayer.io.TCP_Connection;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionCreator;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionPool;
import gent.timdemey.cards.readonlymodel.ReadOnlyServer;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.context.LimitedContext;
import gent.timdemey.cards.utils.Debug;

/**
 * Let a player connect to an online game.
 * 
 * @author Tim
 *
 */
public class C_Connect extends CommandBase
{
    final ReadOnlyServer server;
    final String playerName;
    final UUID localId;
    
    public C_Connect(ReadOnlyServer server, String playerName)
    {
        this.localId = UUID.randomUUID();
        this.server = server;
        this.playerName = playerName;
    }

    @Override
    public void execute(Context context, ContextType type, State state)
    {
        CheckNotContext(type, ContextType.Server);
        if (type == ContextType.UI)
        {
            updateState(state);

            reschedule(ContextType.Client);
        }
        else if (type == ContextType.Client)
        {
            updateState(state);
            
            ICardPlugin plugin = Services.get(ICardPlugin.class);

            if (plugin.getPlayerCount() == 1)
            {
                throw new UnsupportedOperationException();
            }         

            ITcpConnectionListener tcpConnListener = new ClientCommandSchedulingTcpConnectionListener(server.getId(), playerName, localId);
            TCP_ConnectionPool tcpConnPool = new TCP_ConnectionPool(1, tcpConnListener);

            state.setTcpConnectionPool(tcpConnPool);
            state.setTcpConnectionListener(tcpConnListener);

            TCP_ConnectionCreator.connect(tcpConnPool, server.getInetAddress(), server.getTcpPort());
        }
    }
    
    private void updateState(State state)
    {
        state.setLocalName(playerName);
        state.setLocalId(localId);
        state.setServerId(server.getId());
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
        private final UUID serverId;
        private final String clientName;
        private final UUID clientId;
        
        private ClientCommandSchedulingTcpConnectionListener(UUID serverId, String clientName, UUID clientId)
        {
            super(ContextType.Client);
            this.serverId = serverId;
            this.clientName = clientName;
            this.clientId = clientId;
        }

        @Override
        public void onTcpConnectionAdded(TCP_Connection connection, TCP_ConnectionPool connectionPool)
        {
            super.onTcpConnectionAdded(connection, connectionPool);
            connectionPool.bindUUID(serverId, connection);
            
            // if the connection is complete then we can join the game
            CommandBase cmd = new C_JoinGame(clientName, clientId);
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
                CommandBase cmd = new C_OnConnectionLoss(connection, id);
                context.schedule(cmd);
            }
        }
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("serverName", server.getServerName()) +
               Debug.getKeyValue("serverAddress", server.getInetAddress()) + 
               Debug.getKeyValue("serverPort", server.getTcpPort()) +
               Debug.getKeyValue("playerName", playerName) +
               Debug.getKeyValue("localId", localId);
    }
}
