package gent.timdemey.cards.model.commands;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
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
    public final int udpport;   // udp port to listen for clients broadcasting, to discover servers
    public final int tcpport;   // tcp port to accepts clients on that want to join a game
    public final int minconns;  // minimal connections required to start a game
    public final int maxconns;  // maximal connections allowed to the server
    
    public C_CreateServer (String srvname, String srvmsg, int udpport, int tcpport, int minconns, int maxconns)
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
        return true;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
    	if (type == ContextType.UI)
    	{
    		reschedule(ContextType.Server);
    		return;
    	}
    	
    	if (type == ContextType.Client)
    	{
    		throw new IllegalStateException();
    	}
    	// construct answer envelope
		int major = Services.get(ICardPlugin.class).getMajorVersion();
		int minor = Services.get(ICardPlugin.class).getMinorVersion();

		InetAddress addr = null;
		try (final DatagramSocket socket = new DatagramSocket())
		{
			socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			addr = socket.getLocalAddress();
		} catch (SocketException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	//	UUID serverId = state.getServerId();
		C_UDP_Answer cmd_hello = new C_UDP_Answer(srvname, addr, tcpport, major, minor);		
		String json_hello = CommandDtoMapper.toJson(cmd_hello);

		UDP_ServiceAnnouncer udpServAnnouncer = new UDP_ServiceAnnouncer(udpport, this::canAcceptUdpMessage, json_hello);
		
		int playerCount = Services.get(ICardPlugin.class).getPlayerCount();
		C_DenyClient cmd_reject = new C_DenyClient("Server is full");
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
    
    private Boolean canAcceptUdpMessage(String json)
	{
    	try 
    	{
    		CommandBase command = CommandDtoMapper.toCommand(json);	
    		if (!(command instanceof C_UDP_StartServiceRequester))
    		{
    			return false;
    		}
    	}
    	catch (Exception ex)
    	{
    		return false;
    	}
		
		return true;
	}

}
