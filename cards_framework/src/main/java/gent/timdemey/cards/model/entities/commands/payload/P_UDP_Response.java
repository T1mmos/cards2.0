package gent.timdemey.cards.model.entities.commands.payload;

import java.net.InetAddress;
import java.util.UUID;

import gent.timdemey.cards.model.entities.common.PayloadBase;

public class P_UDP_Response extends PayloadBase
{
    public UUID serverId;
    public String serverName;
    public InetAddress inetAddress;
    public int tcpport;
    public int majorVersion;
    public int minorVersion;
}
