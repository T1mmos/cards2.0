package gent.timdemey.cards.model.entities.commands;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.UUID;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.config.Configuration;
import gent.timdemey.cards.model.entities.game.GameState;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.entities.game.Server;
import gent.timdemey.cards.model.entities.game.payload.P_Player;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.netcode.TCP_ConnectionAccepter;
import gent.timdemey.cards.netcode.TCP_ConnectionPool;
import gent.timdemey.cards.netcode.UDP_ServiceAnnouncer;
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

    public C_StartServer(UUID playerId, String playerName, String srvname, String srvmsg, int udpport, int tcpport, boolean autoconnect)
    {
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
        boolean srvCtxtInit = Services.get(IContextService.class).isInitialized(ContextType.Server);
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
            IContextService ctxtServ = Services.get(IContextService.class);
            ctxtServ.initialize(ContextType.Server);

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
                Configuration cfg = new Configuration();
                {
                    cfg.setServerTcpPort(tcpport);
                    cfg.setServerUdpPort(udpport);    
                }  
                
                // create a player
                P_Player pl_player = new P_Player();
                {
                    pl_player.id = playerId;
                    pl_player.name = playerName;    
                }                
                Player player = new Player(pl_player);
                
                // create web service to announce the presence over UDP 
                UDP_ServiceAnnouncer udpServAnnouncer = new UDP_ServiceAnnouncer(udpport);

                // create web service to accept TCP connections
                int playerCount = Services.get(ICardPlugin.class).getPlayerCount();
                CommandSchedulingTcpConnectionListener tcpConnListener = new CommandSchedulingTcpConnectionListener(ContextType.Server);
                TCP_ConnectionPool tcpConnPool = new TCP_ConnectionPool(type.name(), playerCount, tcpConnListener);
                TCP_ConnectionAccepter tcpConnAccepter = new TCP_ConnectionAccepter(tcpConnPool, tcpport);

                // update the state: set server, lobby admin, add player, command history
                Server server = new Server(srvname, addr, tcpport);
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
                state.setCommandHistory(new CommandHistory(true));

                // start the services
                udpServAnnouncer.start();
                tcpConnAccepter.start();

                // schedule a command to have the local player join the server
                if(autoconnect)
                {
                    C_Connect cmd_connect = new C_Connect(playerId, server.id, addr, tcpport, srvname, playerName);
                    schedule(ContextType.UI, cmd_connect);
                }
            }
            catch (Exception ex)
            {
                Logger.error("An error occured while starting the server", ex);

                C_StopServer cmd_stopserver = new C_StopServer();
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
