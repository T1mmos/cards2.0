package gent.timdemey.cards.model.entities.state.payload;

import gent.timdemey.cards.common.Version;
import gent.timdemey.cards.model.entities.common.PayloadBase;
import gent.timdemey.cards.model.entities.state.ServerTCP;

public class P_ServerUDP extends PayloadBase
{
    public ServerTCP server;
    public Version version;
    public int playerCount;
    public int maxPlayerCount;
}
