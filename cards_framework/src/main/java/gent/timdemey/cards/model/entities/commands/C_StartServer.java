package gent.timdemey.cards.model.entities.commands;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.UUID;

import gent.timdemey.cards.ICardPlugin;

import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.config.Configuration;
import gent.timdemey.cards.model.entities.config.ConfigurationFactory;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.Player;
import gent.timdemey.cards.model.entities.state.ServerTCP;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.model.entities.state.StateFactory;
import gent.timdemey.cards.model.net.ITcpConnectionListener;
import gent.timdemey.cards.model.net.NetworkFactory;
import gent.timdemey.cards.model.net.TCP_ConnectionAccepter;
import gent.timdemey.cards.model.net.TCP_ConnectionPool;
import gent.timdemey.cards.model.net.UDP_ServiceAnnouncer;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.utils.Debug;

/**
 * Command that starts a server and automatically joins the current player in
 * the server lobby (as lobby admin). If starting fails then a new command
 * D_StartServerFail is scheduled on the UI context. If starting succeeds then a
 * C_WelcomeClient is scheduled on the UI thread.
 * 
 * @author Tim
 *
 */
public class C_StartServer extends CommandBase
{
    public final UUID playerId;
    public final String playerName; // name of player that started the server, automatically becomes lobby admin id
                                    // and joins the game
    public final String srvname; // server name to broadcast
    public final String srvmsg;
    // public final InetAddress address;
    public final int udpport; // udp port to listen for clients broadcasting, to discover servers
    public final int tcpport; // tcp port to accepts clients on that want to join a game
    public final boolean autoconnect; // whether to automatically connect as client to the server about to be created

    private ConfigurationFactory _ConfigurationFactory;
    private StateFactory _StateFactory;
    private NetworkFactory _NetworkFactory;
    private Logger _Logger;
    private CommandFactory _CommandFactory;
    private ICardPlugin _CardPlugin;

    C_StartServer(
        IContextService contextService, 
        ICardPlugin cardPlugin,
        NetworkFactory networkFactory,
        StateFactory stateFactory,
        CommandFactory commandFactory,
        ConfigurationFactory configurationFactory,
        Logger logger,
        UUID id, UUID playerId, String playerName, String srvname, String srvmsg, int udpport, int tcpport, boolean autoconnect)
    {
        super(contextService, id);
        
        if (playerId == null)
        {
            throw new IllegalArgumentException("playerId");
        }
        if (playerName == null)
        {
            throw new IllegalArgumentException("playerName");
        }
        if (srvname == null || srvname.isEmpty())
        {
            throw new IllegalArgumentException("srvname");
        }        
        if (udpport <= 1024)
        {
            throw new IllegalArgumentException("serverUdpPort");
        }
        if (tcpport <= 1024)
        {
            throw new IllegalArgumentException("serverTcpPort");
        }
        if (udpport == tcpport)
        {
            throw new IllegalArgumentException("serverTcpPort equals serverUdpPort");
        }
        
        this._CardPlugin = cardPlugin;
        this._NetworkFactory = networkFactory;
        this._CommandFactory = commandFactory;
        this._StateFactory = stateFactory;        
        this._ConfigurationFactory = configurationFactory;
        this._Logger = logger;
        
        this.playerId = playerId;
        this.playerName = playerName;
        this.srvname = srvname;
        this.srvmsg = srvmsg;
        this.udpport = udpport;
        this.tcpport = tcpport;
        this.autoconnect = autoconnect;
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        boolean srvCtxtInit = _ContextService.isInitialized(ContextType.Server);
        if(type == ContextType.UI)
        {
            if(srvCtxtInit)
            {
                return CanExecuteResponse.no("Server context is already initialized");
            }
        }
        else if(type == ContextType.Server)
        {
            if(!srvCtxtInit)
            {
                return CanExecuteResponse.no("Server context is not initialized");
            }
        }

        return CanExecuteResponse.yes();
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        if(type == ContextType.UI)
        {
            _ContextService.initialize(ContextType.Server);

            schedule(ContextType.Server, this);
        }
        else
        {
            try
            {
                // determine the local IP
                InetAddress addr = null;
                try (final DatagramSocket socket = new DatagramSocket())
                {
                    socket.connect(InetAddress.getByName("8.8.8.8"), tcpport);
                    addr = socket.getLocalAddress();
                }
                
                // create a configuration
                Configuration cfg = _ConfigurationFactory.CreateConfiguration();
                {
                    cfg.setServerTcpPort(tcpport);
                    cfg.setServerUdpPort(udpport);    
                }  
                
                // create a player             
                Player player = _StateFactory.CreatePlayer(playerId, playerName);
                
                // create web service to announce the presence over UDP 
                UDP_ServiceAnnouncer udpServAnnouncer = _NetworkFactory.CreateUDPServiceAnnouncer(udpport);

                // create web service to accept TCP connections
                int playerCount = _CardPlugin.getPlayerCount();
                ITcpConnectionListener tcpConnListener = _CommandFactory.CreateCommandSchedulingTcpConnectionListener(ContextType.Server);
                TCP_ConnectionPool tcpConnPool = _NetworkFactory.CreateTCPConnectionPool(type.name(), playerCount, tcpConnListener);
                TCP_ConnectionAccepter tcpConnAccepter = _NetworkFactory.CreateTCPConnectionAccepter(tcpConnPool, tcpport);

                // update the state: set server, lobby admin, add player, command history
                ServerTCP server = _StateFactory.CreateServerTCP(srvname, addr, tcpport);
                state.setServer(server);
                state.setServerMessage(srvmsg);
                state.setLocalId(server.id);
                state.setLobbyAdminId(playerId);
                state.setGameState(GameState.Lobby);
                state.setConfiguration(cfg);
                state.getPlayers().add(player);
                state.setUdpServiceAnnouncer(udpServAnnouncer);
                state.setTcpConnectionAccepter(tcpConnAccepter);
                state.setTcpConnectionPool(tcpConnPool);
                
                boolean canUndo = false;       // multiplayer
                boolean canRemove = true;      // must be able to correct history due to network lag based on server decision
                state.setCommandHistory(_StateFactory.CreateCommandHistory(canUndo, canRemove));

                // start the services
                udpServAnnouncer.start();
                tcpConnAccepter.start();

                // schedule a command to have the local player join the server
                if(autoconnect)
                {
                    C_Connect cmd_connect;
                    cmd_connect = _CommandFactory.CreateConnect(playerId, server.id, addr, tcpport, srvname, playerName);
                    schedule(ContextType.UI, cmd_connect);
                }
            }
            catch (Exception ex)
            {
                _Logger.error("An error occured while starting the server", ex);

                C_StopServer cmd_stopserver = _CommandFactory.CreateStopServer();
                run(cmd_stopserver);
            }
        }

    }
    
    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("srvname", srvname) + Debug.getKeyValue("srvmsg", srvmsg) + Debug.getKeyValue("serverUdpPort", udpport) + Debug.getKeyValue(
            "serverTcpPort", tcpport);
    }
}
