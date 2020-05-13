package gent.timdemey.cards.serialization.dto.game;

import gent.timdemey.cards.serialization.dto.EntityBaseDto;

public class ServerDto extends EntityBaseDto
{
    public String serverName;
    public String inetAddress;
    public int tcpport;
}
