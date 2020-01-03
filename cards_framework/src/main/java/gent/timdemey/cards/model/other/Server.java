package gent.timdemey.cards.model.other;

import java.net.InetAddress;

public class Server
{
	public final String serverName;
	public final InetAddress inetAddress;
	public final int tcpport;
	
	public Server(String serverName, InetAddress inetAddress, int tcpport) 
	{
		this.serverName = serverName;
		this.inetAddress = inetAddress;
		this.tcpport = tcpport;
	}
}
