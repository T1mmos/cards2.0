package gent.timdemey.cards.multiplayer;

import com.google.common.base.Preconditions;

public class CreateServerInfo {
    public final String srvname; // server name to broadcast
    public final String srvmsg;
   // public final InetAddress address;
    public final int udpport;   // udp port to listen for clients broadcasting, to discover servers
    public final int tcpport;   // tcp port to accepts clients on that want to join a game
    public final int minconns;  // minimal connections required to start a game
    public final int maxconns;  // maximal connections allowed to the server
    
    public CreateServerInfo (String srvname, String srvmsg, int udpport, int tcpport, int minconns, int maxconns)
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
}
