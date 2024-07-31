package gent.timdemey.cards.model.entities.commands;

import java.net.InetAddress;
import java.util.UUID;

import gent.timdemey.cards.ICardPlugin;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.ServerTCP;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.model.entities.state.StateFactory;
import gent.timdemey.cards.model.entities.commands.payload.P_Connect;
import gent.timdemey.cards.model.net.ITcpConnectionListener;
import gent.timdemey.cards.model.net.NetworkFactory;
import gent.timdemey.cards.model.net.TCP_ConnectionPool;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
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
    private final ICardPlugin _CardPlugin;
    private final StateFactory _StateFactory;
    private final NetworkFactory _NetworkFactory;
    private final CommandFactory _CommandFactory;
        
    public C_Connect(
        IContextService contextService,
        ICardPlugin cardPlugin, 
        NetworkFactory networkFactory,
        StateFactory stateFactory,
        CommandFactory commandFactory,
        P_Connect parameters)
    {
        super(contextService, parameters);
        this._CardPlugin = cardPlugin;
        this._NetworkFactory = networkFactory;
        this._StateFactory = stateFactory;
        this._CommandFactory = commandFactory;
        
        this.playerId = parameters.playerId;
        this.serverId = parameters.serverId;
        this.serverInetAddress = parameters.serverInetAddress;
        this.serverTcpPort = parameters.serverTcpPort;
        this.serverName = parameters.serverName;
        this.playerName = parameters.playerName;
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        if (_CardPlugin.getPlayerCount() == 1)
        {
            return CanExecuteResponse.no("The plugin indicates this is a single player game");
        } 
        
        return CanExecuteResponse.yes();
    }
    
    @Override
    public void execute(Context context, ContextType type, State state)
    {
        CheckNotContext(type, ContextType.Server);
        if (type == ContextType.UI)
        {
            ServerTCP server = _StateFactory.CreateServerTCP(serverId, serverName, serverInetAddress, serverTcpPort);
            state.setServer(server);
            state.setLocalName(playerName);
            
            ITcpConnectionListener tcpConnListener = _CommandFactory.CreateCommandSchedulingTcpConnectionListener(ContextType.UI);
            TCP_ConnectionPool tcpConnPool = _NetworkFactory.CreateTCPConnectionPool(type.name(), 1, tcpConnListener);

            state.setTcpConnectionPool(tcpConnPool);

            tcpConnPool.addConnection(serverInetAddress, serverTcpPort);            
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
