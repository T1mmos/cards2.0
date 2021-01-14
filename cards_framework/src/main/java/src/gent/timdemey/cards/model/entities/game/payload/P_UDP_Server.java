package gent.timdemey.cards.model.entities.game.payload;

import gent.timdemey.cards.common.Version;
import gent.timdemey.cards.model.entities.common.PayloadBase;
import gent.timdemey.cards.model.entities.game.Server;

public class P_UDP_Server extends PayloadBase
{
    public Server server;
    public Version version;
    public int playerCount;
    public int maxPlayerCount;
}
