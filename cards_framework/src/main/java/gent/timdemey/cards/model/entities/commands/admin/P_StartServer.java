package gent.timdemey.cards.model.entities.commands.admin;

import gent.timdemey.cards.model.entities.commands.CommandPayloadBase;

public class P_StartServer extends CommandPayloadBase
{
    public String localName;
    public String srvname;
    public String srvmsg;
    public int udpPort;
    public int tcpPort;
    public boolean autoconnect;
}
