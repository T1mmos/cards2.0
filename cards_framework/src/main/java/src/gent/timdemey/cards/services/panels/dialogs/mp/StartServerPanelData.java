package gent.timdemey.cards.services.panels.dialogs.mp;

public class StartServerPanelData
{
    public final String srvname; // server name to broadcast
    public final String srvmsg;
    public final int udpport; // udp port to listen for clients broadcasting, to discover servers
    public final int tcpport; // tcp port to accepts clients on that want to join a game
    public final boolean autoconnect;

    public StartServerPanelData(String srvname, String srvmsg, int udpport, int tcpport, boolean autoconnect)
    {
        this.srvname = srvname;
        this.srvmsg = srvmsg;
        this.udpport = udpport;
        this.tcpport = tcpport;
        this.autoconnect = autoconnect;
    }
}
