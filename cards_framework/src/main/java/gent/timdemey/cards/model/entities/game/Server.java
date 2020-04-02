package gent.timdemey.cards.model.entities.game;

import java.net.InetAddress;

import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.entities.game.payload.P_Server;

public class Server extends EntityBase
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

    public Server(P_Server pl)
    {
        super(pl);
        
        this.serverName = pl.serverName;
        this.inetAddress = pl.inetAddress;
        this.tcpport = pl.tcpport;
    }
    
    @Override
    public String toDebugString()
    {
        return String.format("ServerName=%s,InetAddress=%s,TcpPort=%s", serverName, inetAddress.getHostAddress(), tcpport);
    }
}
