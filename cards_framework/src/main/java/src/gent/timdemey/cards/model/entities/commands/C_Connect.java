package gent.timdemey.cards.model.entities.commands;

import java.net.InetAddress;
import java.util.UUID;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.game.Server;
import gent.timdemey.cards.model.entities.game.payload.P_Server;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.netcode.CommandSchedulingTcpConnectionListener;
import gent.timdemey.cards.netcode.ITcpConnectionListener;
import gent.timdemey.cards.netcode.TCP_Connection;
import gent.timdemey.cards.netcode.TCP_ConnectionPool;
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
    final UUID playerId;
    final UUID serverId;
    final InetAddress serverInetAddress;
    final int serverTcpPort;
    final String serverName;
    final String playerName;
    
    public C_Connect(UUID playerId, UUID serverId, InetAddress serverInetAddress, int serverTcpPort, String serverName, String playerName)
    {
        this.playerId = playerId;
        this.serverId = serverId;
        this.serverInetAddress = serverInetAddress;
        this.serverTcpPort = serverTcpPort;
        this.serverName = serverName;
        this.playerName = playerName;
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        return CanExecuteResponse.yes();
    }
    
    @Override
    public void preExecute(Context context, ContextType type, State state)
    {
        CheckNotContext(type, ContextType.Server);
        if (type == ContextType.UI)
        {
            ICardPlugin plugin = Services.get(ICardPlugin.class);
            if (plugin.getPlayerCount() == 1)
            {
                throw new UnsupportedOperationException();
            }  
            
            if (!state.getServers().contains(serverId))
            {
                P_Server pl = new P_Server();
                pl.id = serverId;
                pl.inetAddress = serverInetAddress;
                pl.serverName = serverName;
                pl.tcpport = serverTcpPort;
                state.getServers().add(new Server(pl));
            }
            state.setServerId(serverId);
            
            ITcpConnectionListener tcpConnListener = new ClientCommandSchedulingTcpConnectionListener(serverId, playerName, state.getLocalId());
            TCP_ConnectionPool tcpConnPool = new TCP_ConnectionPool(type.name(), 1, tcpConnListener);

            state.setTcpConnectionPool(tcpConnPool);

            tcpConnPool.addConnection(serverInetAddress, serverTcpPort);            
        }
    }

    private static class ClientCommandSchedulingTcpConnectionListener extends CommandSchedulingTcpConnectionListener
    {
        private final UUID serverId;
        private final String clientName;
        private final UUID clientId;
        
        private ClientCommandSchedulingTcpConnectionListener(UUID serverId, String clientName, UUID clientId)
        {
            super(ContextType.UI);
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
            CommandBase cmd = new C_EnterLobby(clientName, clientId);
            IContextService contextServ = Services.get(IContextService.class);
            LimitedContext context = contextServ.getContext(ContextType.UI);
            context.schedule(cmd);
        }

        @Override
        public void onTcpConnectionRemotelyClosed(UUID id, TCP_Connection connection)
        {
            LimitedContext context = Services.get(IContextService.class).getContext(ContextType.UI);
            if (id != null)
            {
                CommandBase cmd = new C_OnServerConnectionLost();
                context.schedule(cmd);
            }
        }
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("serverAddress", serverInetAddress) + 
               Debug.getKeyValue("serverPort", serverTcpPort) +
               Debug.getKeyValue("playerName", playerName);
    }
}
