package gent.timdemey.cards.model.commands;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.UUID;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.multiplayer.Server;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.multiplayer.io.CommandSchedulingTcpConnectionListener;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionAccepter;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionPool;
import gent.timdemey.cards.multiplayer.io.UDP_ServiceAnnouncer;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_CreateServer extends CommandBase
{
    public final String srvname; // server name to broadcast
    public final String srvmsg;
    // public final InetAddress address;
    public final int udpport; // udp port to listen for clients broadcasting, to discover servers
    public final int tcpport; // tcp port to accepts clients on that want to join a game
    public final int minconns; // minimal connections required to start a game
    public final int maxconns; // maximal connections allowed to the server

    public C_CreateServer(String srvname, String srvmsg, int udpport, int tcpport, int minconns, int maxconns)
    {
        Preconditions.checkArgument(srvname != null && !srvname.isEmpty());
        Preconditions.checkArgument(udpport > 1024);
        Preconditions.checkArgument(tcpport > 1024);
        Preconditions.checkArgument(udpport != tcpport);
        Preconditions.checkArgument(minconns > 1);
        Preconditions.checkArgument(maxconns <= 4);
        Preconditions.checkArgument(minconns <= maxconns);

        this.srvname = srvname;
        this.srvmsg = srvmsg;
        // this.address = address;
        this.udpport = udpport;
        this.tcpport = tcpport;
        this.minconns = minconns;
        this.maxconns = maxconns;
    }

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        boolean srvCtxtInit = Services.get(IContextService.class).isInitialized(ContextType.Server);
        if (type == ContextType.UI)
        {
            return !srvCtxtInit;
        }
        else if (type == ContextType.Client)
        {
            return false;
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
        CheckNotContext(type, ContextType.Client);
        
        if (type == ContextType.UI)
        {
            Services.get(IContextService.class).initialize(ContextType.Server);
            
            reschedule(ContextType.Server);
            return;
        }

        
        // construct answer envelope
        int major = Services.get(ICardPlugin.class).getMajorVersion();
        int minor = Services.get(ICardPlugin.class).getMinorVersion();

        InetAddress addr = null;
        int tcpport = 10002;
        try (final DatagramSocket socket = new DatagramSocket())
        {
            socket.connect(InetAddress.getByName("8.8.8.8"), tcpport);
            addr = socket.getLocalAddress();
        }
        catch (SocketException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (UnknownHostException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        

        // create the server
        Server server = new Server(srvname, addr, tcpport);
        state.getServers().add(server);
        state.setServerId(server.id);

        // create web services to announce the presence over UDP and to accept TCP connections
        UDP_ServiceAnnouncer udpServAnnouncer = new UDP_ServiceAnnouncer(udpport);
        
        int playerCount = Services.get(ICardPlugin.class).getPlayerCount();
        C_DenyClient cmd_reject = new C_DenyClient(UUID.randomUUID(), "Server is full");
        String json_reject = CommandDtoMapper.toJson(cmd_reject);

        CommandSchedulingTcpConnectionListener tcpConnListener = new CommandSchedulingTcpConnectionListener(ContextType.Server);
        TCP_ConnectionPool tcpConnPool = new TCP_ConnectionPool(playerCount, tcpConnListener);
        TCP_ConnectionAccepter tcpConnAccepter = new TCP_ConnectionAccepter(tcpConnPool, this, json_reject);

        state.setUdpServiceAnnouncer(udpServAnnouncer);
        state.setTcpConnectionAccepter(tcpConnAccepter);
        state.setTcpConnectionListener(tcpConnListener);
        state.setTcpConnectionPool(tcpConnPool);

        udpServAnnouncer.start();
        tcpConnAccepter.start();
    }

}
