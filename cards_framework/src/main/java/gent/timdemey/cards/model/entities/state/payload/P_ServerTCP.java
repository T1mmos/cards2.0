package gent.timdemey.cards.model.entities.state.payload;

import java.net.InetAddress;

import gent.timdemey.cards.model.entities.common.PayloadBase;

public class P_ServerTCP extends PayloadBase
{
    public String serverName;
    public InetAddress inetAddress;
    public int tcpport;
}
