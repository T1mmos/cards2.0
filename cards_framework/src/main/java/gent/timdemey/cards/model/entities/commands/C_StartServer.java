package gent.timdemey.cards.model.entities.commands;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.UUID;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Main;
import gent.timdemey.cards.Starter;
import gent.timdemey.cards.di.Container;

import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_StartServer;
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
import gent.timdemey.cards.model.net.UDP_Source;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.utils.Debug;
import gent.timdemey.cards.model.net.IUdpMessageListener;
import gent.timdemey.cards.di.IContainerService;

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
    private CommandDtoMapper _CommandDtoMapper;
    private Starter _Starter;

    public C_StartServer(
        Container container,
        ICardPlugin cardPlugin,
        
        NetworkFactory networkFactory,
        StateFactory stateFactory,
        CommandFactory commandFactory,
        ConfigurationFactory configurationFactory,
        CommandDtoMapper commandDtoMapper,
        Logger logger,
        Starter starter,
        P_StartServer parameters)
    {
        super(container, parameters);
        
        if (parameters.localId == null)
        {
            throw new IllegalArgumentException("localId");
        }
        if (parameters.localName == null)
        {
            throw new IllegalArgumentException("playerName");
        }
        if (parameters.srvname == null || parameters.srvname.isEmpty())
        {
            throw new IllegalArgumentException("srvname");
        }        
        if (parameters.udpPort <= 1024)
        {
            throw new IllegalArgumentException("serverUdpPort");
        }
        if (parameters.tcpPort <= 1024)
        {
            throw new IllegalArgumentException("serverTcpPort");
        }
        if (parameters.udpPort == parameters.tcpPort)
        {
            throw new IllegalArgumentException("serverTcpPort equals serverUdpPort");
        }
        
        this._CardPlugin = cardPlugin;
        this._NetworkFactory = networkFactory;
        this._CommandFactory = commandFactory;
        this._StateFactory = stateFactory;        
        this._ConfigurationFactory = configurationFactory;
        this._CommandDtoMapper = commandDtoMapper;
        this._Logger = logger;
        this._Starter = starter;
        
        this.playerId = parameters.localId;
        this.playerName = parameters.localName;
        this.srvname = parameters.srvname;
        this.srvmsg = parameters.srvmsg;
        this.udpport = parameters.udpPort;
        this.tcpport = parameters.tcpPort;
        this.autoconnect = parameters.autoconnect;
    }

    @Override
    public CanExecuteResponse canExecute()
    {
        boolean srvCtxtInit = _ContainerService.isInitialized(ContextType.Server);
        if(_ContextType == ContextType.UI)
        {
            if(srvCtxtInit)
            {
                return CanExecuteResponse.no("Server context is already initialized");
            }
        }
        else if(_ContextType == ContextType.Server)
        {
            if(!srvCtxtInit)
            {
                return CanExecuteResponse.no("Server context is not initialized");
            }
        }

        return CanExecuteResponse.yes();
    }

    @Override
    public void execute()
    {
        if(_ContextType == ContextType.UI)
        {
            _Starter.startServer();
            
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
                TCP_ConnectionPool tcpConnPool = _NetworkFactory.CreateTCPConnectionPool(_ContextType.name(), playerCount, tcpConnListener);
                TCP_ConnectionAccepter tcpConnAccepter = _NetworkFactory.CreateTCPConnectionAccepter(tcpConnPool, tcpport);

                // update the state: set server, lobby admin, add player, command history
                ServerTCP server = _StateFactory.CreateServerTCP(srvname, addr, tcpport);
                _State.setServer(server);
                _State.setServerMessage(srvmsg);
                _State.setLocalId(server.id);
                _State.setLobbyAdminId(playerId);
                _State.setGameState(GameState.Lobby);
                _State.setConfiguration(cfg);
                _State.getPlayers().add(player);
                _State.setUdpServiceAnnouncer(udpServAnnouncer);
                _State.setTcpConnectionAccepter(tcpConnAccepter);
                _State.setTcpConnectionPool(tcpConnPool);
                
                boolean canUndo = false;       // multiplayer
                boolean canRemove = true;      // must be able to correct history due to network lag based on server decision
                _State.setCommandHistory(_StateFactory.CreateCommandHistory(canUndo, canRemove));

                // start the services
                udpServAnnouncer.start(new UDPListener());
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
    
    private class UDPListener implements IUdpMessageListener
    {

        @Override
        public void OnMessageReceived(InetAddress sourceAddress, int sourcePort, String msg) 
        {
            _Logger.error("Received some data, trying to parse it as a C_UDP_Request...");
            CommandBase command = null;
            try
            {
                command = _CommandDtoMapper.toCommand(msg);
            }
            catch (Exception ex)
            {
            }

            if (command == null)
            {
                _Logger.error("Ignoring the received data as it could not be parsed as a command.");
                return;
            }
            if (!(command instanceof C_UDP_Request))
            {
                _Logger.error("Ignoring the received data as the command is not a C_UDP_Request.");
                return;
            }

            // schedule the UDP request command
            C_UDP_Request udpRequestCmd = (C_UDP_Request) command;
            UDP_Source udpSource = new UDP_Source(sourceAddress, sourcePort);
            udpRequestCmd.setSourceUdp(udpSource);

            _CommandExecutor.run(udpRequestCmd);
        }        
    }
}
