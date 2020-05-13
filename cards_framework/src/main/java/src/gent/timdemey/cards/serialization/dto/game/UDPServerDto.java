package gent.timdemey.cards.serialization.dto.game;

import gent.timdemey.cards.serialization.dto.EntityBaseDto;

public class UDPServerDto extends EntityBaseDto
{
    public ServerDto server;
    public int majorVersion;
    public int minorVersion;
    public int playerCount;
    public int maxPlayerCount;
}
