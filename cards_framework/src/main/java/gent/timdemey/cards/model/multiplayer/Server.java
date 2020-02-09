package gent.timdemey.cards.model.multiplayer;

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
    
    @Override
    public String toDebugString()
    {
        return String.format("ServerName=%s,InetAddress=%s,TcpPort=%s", serverName, inetAddress.getHostAddress(), tcpport);
    }
}
