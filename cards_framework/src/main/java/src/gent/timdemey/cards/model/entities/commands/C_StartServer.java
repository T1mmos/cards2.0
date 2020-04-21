package gent.timdemey.cards.model.entities.commands;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.UUID;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.entities.game.Server;
import gent.timdemey.cards.model.entities.game.payload.P_Player;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.netcode.CommandSchedulingTcpConnectionListener;
import gent.timdemey.cards.netcode.TCP_ConnectionAccepter;
import gent.timdemey.cards.netcode.TCP_ConnectionPool;
import gent.timdemey.cards.netcode.UDP_ServiceAnnouncer;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.ISerializationService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.utils.Debug;

/**
 * Command that starts a server and automatically joins the current player in the server lobby (as lobby admin).
 * If starting fails then a new command D_StartServerFail is scheduled on the UI context. If starting succeeds then
 * a C_WelcomeClient is scheduled on the UI thread.
 * @author Tim
 *
 */
public class C_StartServer extends CommandBase
{
    public final UUID playerId;
    public final String playerName; // name of player that started the server, automatically becomes lobby admin id and joins the game
    public final String srvname; // server name to broadcast
    public final String srvmsg;
    // public final InetAddress address;
    public final int udpport; // udp port to listen for clients broadcasting, to discover servers
    public final int tcpport; // tcp port to accepts clients on that want to join a game
    public final int minconns; // minimal connections required to start a game
    public final int maxconns; // maximal connections allowed to the server
    public final boolean autoconnect; // whether to automatically connect as client to the server about to be created

    public C_StartServer(UUID playerId, String playerName, String srvname, String srvmsg, int udpport, int tcpport, int minconns, int maxconns, boolean autoconnect)
    {
        Preconditions.checkArgument(playerId != null);
        Preconditions.checkArgument(playerName != null);
        Preconditions.checkArgument(srvname != null && !srvname.isEmpty());
        Preconditions.checkArgument(udpport > 1024);
        Preconditions.checkArgument(tcpport > 1024);
        Preconditions.checkArgument(udpport != tcpport);
        Preconditions.checkArgument(minconns > 1);
        Preconditions.checkArgument(maxconns <= 4);
        Preconditions.checkArgument(minconns <= maxconns);

        this.playerId = playerId;
        this.playerName = playerName;
        this.srvname = srvname;
        this.srvmsg = srvmsg;
        this.udpport = udpport;
        this.tcpport = tcpport;
        this.minconns = minconns;
        this.maxconns = maxconns;
        this.autoconnect = autoconnect;
    }

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        boolean srvCtxtInit = Services.get(IContextService.class).isInitialized(ContextType.Server);
        if (type == ContextType.UI)
        {
            return !srvCtxtInit;
        }
        else if (type == ContextType.Server)
        {
            return srvCtxtInit;
        }

        return false;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {        
        if (type == ContextType.UI)
        {
            IContextService ctxtServ = Services.get(IContextService.class);
            
            ctxtServ.initialize(ContextType.Server);
            
            state.setLocalName(playerName);
            
            reschedule(ContextType.Server);
        }
        else
        {
            try 
            {
                InetAddress addr = null;
                try (final DatagramSocket socket = new DatagramSocket())
                {
                    socket.connect(InetAddress.getByName("8.8.8.8"), tcpport);
                    addr = socket.getLocalAddress();
                }
                
                // update the state: set server, lobby admin, add player
                Server server = new Server(srvname, addr, tcpport);
                state.getServers().add(server);
                state.setServerId(server.id);
                state.setLocalId(server.id);
                state.setLobbyAdminId(playerId);
                P_Player pl_player = new P_Player();
                pl_player.id = playerId;
                pl_player.name = playerName;
                Player player = new Player(pl_player);
                state.getPlayers().add(player);

                // create web services to announce the presence over UDP and to accept TCP connections
                UDP_ServiceAnnouncer udpServAnnouncer = new UDP_ServiceAnnouncer(udpport);
                
                int playerCount = Services.get(ICardPlugin.class).getPlayerCount();
                C_DenyClient cmd_reject = new C_DenyClient();
                
                CommandDtoMapper dtoMapper = Services.get(ISerializationService.class).getCommandDtoMapper();
                String json_reject = dtoMapper.toJson(cmd_reject);

                CommandSchedulingTcpConnectionListener tcpConnListener = new CommandSchedulingTcpConnectionListener(ContextType.Server);
                TCP_ConnectionPool tcpConnPool = new TCP_ConnectionPool(playerCount, tcpConnListener);
                TCP_ConnectionAccepter tcpConnAccepter = new TCP_ConnectionAccepter(tcpConnPool, tcpport, json_reject);

                state.setUdpServiceAnnouncer(udpServAnnouncer);
                state.setTcpConnectionAccepter(tcpConnAccepter);
                state.setTcpConnectionListener(tcpConnListener);
                state.setTcpConnectionPool(tcpConnPool);

                udpServAnnouncer.start();
                tcpConnAccepter.start();
                
                if (autoconnect)
                {
                    C_Connect cmd_connect = new C_Connect(playerId, server.id, addr, tcpport, srvname, playerName);
                    schedule(ContextType.UI, cmd_connect);
                }                
            }
            catch (Exception ex)
            {
                cleanResources(state);
            }
        }
       
    }

    private void cleanResources(State state)
    {
        // clean up
        try 
        {
            if (state.getUdpServiceAnnouncer() != null)
            {
                state.getUdpServiceAnnouncer().stop();
                state.setUdpServiceAnnouncer(null);
            }
            
        }
        catch (Exception ex2)
        {
        }
        
        try 
        {
            if (state.getTcpConnectionAccepter() != null)
            {
                state.getTcpConnectionAccepter().stop();
                state.setTcpConnectionAccepter(null);
            }
        }
        catch (Exception ex2)
        {
        }
        
        try 
        {
            if (state.getTcpConnectionListener() != null)
            {
                state.setTcpConnectionAccepter(null);
            }
        }
        catch (Exception ex2)
        {
        }
        
        try 
        {
            if (state.getTcpConnectionPool() != null)
            {
                state.setTcpConnectionPool(null);
            }
        }
        catch (Exception ex2)
        {
        }
    }
    
    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("srvname", srvname) + 
               Debug.getKeyValue("srvmsg", srvmsg) + 
               Debug.getKeyValue("udpport", udpport) + 
               Debug.getKeyValue("tcpport", tcpport) + 
               Debug.getKeyValue("minconns", minconns) + 
               Debug.getKeyValue("maxconns", maxconns);
    }
}
