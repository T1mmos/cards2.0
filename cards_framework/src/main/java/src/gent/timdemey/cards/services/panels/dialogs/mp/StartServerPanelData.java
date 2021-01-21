package gent.timdemey.cards.services.panels.dialogs.mp;

public class StartServerPanelData
{
    public final String srvname; // server name to broadcast
    public final String srvmsg;
    public final boolean autoconnect;

    public StartServerPanelData(String srvname, String srvmsg, boolean autoconnect)
    {
        this.srvname = srvname;
        this.srvmsg = srvmsg;
        this.autoconnect = autoconnect;
    }
}
