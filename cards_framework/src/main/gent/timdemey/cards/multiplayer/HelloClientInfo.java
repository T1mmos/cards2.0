package gent.timdemey.cards.multiplayer;

import java.net.InetAddress;

public class HelloClientInfo {
    public final String srvname; // server name as announced by the server, for UI purposes
    public final InetAddress address; // ip address of server
    public final int tcpport;   // tcp port of server
    
    public HelloClientInfo (String srvname, InetAddress address, int tcpport)
    {
        this.srvname = srvname;
        this.address = address;
        this.tcpport = tcpport;
    }
}
