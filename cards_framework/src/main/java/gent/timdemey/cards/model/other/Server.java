package gent.timdemey.cards.model.other;

import java.net.InetAddress;
import java.util.UUID;

import gent.timdemey.cards.model.EntityBase;

public class Server extends EntityBase
{
	public final String serverName;
	public final InetAddress inetAddress;
	public final int tcpport;
	
	public Server(String serverName, InetAddress inetAddress, int tcpport) 
	{
		this(UUID.randomUUID(), serverName, inetAddress, tcpport);
	}
	
	public Server(UUID id, String serverName, InetAddress inetAddress, int tcpport) 
	{
		super(id);
		this.serverName = serverName;
		this.inetAddress = inetAddress;
		this.tcpport = tcpport;
	}
}
