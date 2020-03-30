package gent.timdemey.cards.multiplayer.io;

import java.net.InetAddress;

public class UDP_Source
{
    public final InetAddress inetAddress;
    public int tcpPort;
    
    public UDP_Source(InetAddress inetAddress, int tcpPort)
    {
        this.inetAddress = inetAddress;
        this.tcpPort = tcpPort;
    }
}
