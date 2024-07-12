package gent.timdemey.cards.model.entities.game.payload;

import java.net.InetAddress;

import gent.timdemey.cards.model.entities.common.PayloadBase;

public class P_Server extends PayloadBase
{
    public String serverName;
    public InetAddress inetAddress;
    public int tcpport;
}
