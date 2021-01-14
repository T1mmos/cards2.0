package gent.timdemey.cards.model.entities.game;

import gent.timdemey.cards.common.Version;
import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.entities.game.payload.P_UDP_Server;
import gent.timdemey.cards.utils.Debug;

public class UDPServer extends EntityBase
{
    public final Server server;
    public final Version version;
    public final int playerCount;
    public final int maxPlayerCount;

    public UDPServer(Server server, Version version, int playerCount, int maxPlayerCount)
    {
        this.server = server;
        this.version = version;
        this.playerCount = playerCount;
        this.maxPlayerCount = maxPlayerCount;
    }

    public UDPServer(P_UDP_Server pl)
    {
        super(pl);

        this.server = pl.server;
        this.version = pl.version;
        this.playerCount = pl.playerCount;
        this.maxPlayerCount = pl.maxPlayerCount;
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("server", server) + Debug.getKeyValue("version", version)
             + Debug.getKeyValue("playerCount", playerCount) + Debug.getKeyValue("maxPlayerCount", maxPlayerCount);
    }

}
