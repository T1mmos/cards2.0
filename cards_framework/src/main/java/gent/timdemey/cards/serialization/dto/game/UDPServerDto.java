package gent.timdemey.cards.serialization.dto.game;

import gent.timdemey.cards.serialization.dto.EntityBaseDto;
import gent.timdemey.cards.serialization.dto.common.VersionDto;

public class UDPServerDto extends EntityBaseDto
{
    public ServerDto server;
    public VersionDto version;
    public int playerCount;
    public int maxPlayerCount;
}
