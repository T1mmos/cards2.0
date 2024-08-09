package gent.timdemey.cards.model.entities.commands.net;

import java.net.InetAddress;
import java.util.UUID;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CommandFactory;

import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.ServerTCP;
import gent.timdemey.cards.model.entities.state.StateFactory;
import gent.timdemey.cards.model.net.NetworkFactory;
import gent.timdemey.cards.model.net.TCP_ConnectionPool;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.utils.Debug;

/**
 * Let a player connect to an online game.
 * 
 * @author Tim
 *
 */
public class C_TCP_ClientConnect extends CommandBase<P_TCP_ClientConnect>
{
    final UUID serverId;
    final InetAddress serverInetAddress;
    final int serverTcpPort;
    final String serverName;
    final String playerName;
    private final ICardPlugin _CardPlugin;
    private final StateFactory _StateFactory;
    private final NetworkFactory _NetworkFactory;
    private final CommandFactory _CommandFactory;
        
    public C_TCP_ClientConnect(
        Container container,
        ICardPlugin cardPlugin, 
        NetworkFactory networkFactory,
        StateFactory stateFactory,
        CommandFactory commandFactory,
        P_TCP_ClientConnect parameters)
    {
        super(container, parameters);
        this._CardPlugin = cardPlugin;
        this._NetworkFactory = networkFactory;
        this._StateFactory = stateFactory;
        this._CommandFactory = commandFactory;
        
        this.serverId = parameters.serverId;
        this.serverInetAddress = parameters.serverInetAddress;
        this.serverTcpPort = parameters.serverTcpPort;
        this.serverName = parameters.serverName;
        this.playerName = parameters.playerName;
    }

    @Override
    public CanExecuteResponse canExecute()
    {
        if (_CardPlugin.getPlayerCount() == 1)
        {
            return CanExecuteResponse.no("The plugin indicates this is a single player game");
        } 
        
        return CanExecuteResponse.yes();
    }
    
    @Override
    public void execute()
    {
        if (_ContextType == ContextType.UI)
        {
            ServerTCP server = _StateFactory.CreateServerTCP(serverId, serverName, serverInetAddress, serverTcpPort);
            _State.setServer(server);
            _State.setLocalName(playerName);
            
            CommandSchedulingTcpConnectionListener tcpConnListener = _CommandFactory.CreateCommandSchedulingTcpConnectionListener(ContextType.UI);
            TCP_ConnectionPool tcpConnPool = _NetworkFactory.CreateTCPConnectionPool(_ContextType.name(), 1, tcpConnListener);

            _State.setTcpConnectionPool(tcpConnPool);

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
