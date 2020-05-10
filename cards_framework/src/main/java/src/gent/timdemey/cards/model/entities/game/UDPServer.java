package gent.timdemey.cards.model.entities.game;

import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.entities.game.payload.P_UDP_Server;
import gent.timdemey.cards.utils.Debug;

public class UDPServer extends EntityBase
{
    public final Server server;
    public final int majorVersion;
    public final int minorVersion;
    public final int playerCount;
    public final int maxPlayerCount;

    public UDPServer(Server server, int majorVersion, int minorVersion, int playerCount, int maxPlayerCount)
    {
        this.server = server;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.playerCount = playerCount;
        this.maxPlayerCount = maxPlayerCount;
    }

    public UDPServer(P_UDP_Server pl)
    {
        super(pl);

        this.server = pl.server;
        this.majorVersion = pl.majorVersion;
        this.minorVersion = pl.minorVersion;
        this.playerCount = pl.playerCount;
        this.maxPlayerCount = pl.maxPlayerCount;
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("server", server) + Debug.getKeyValue("majorVersion", majorVersion) + Debug.getKeyValue("minorVersion", minorVersion)
                + Debug.getKeyValue("playerCount", playerCount) + Debug.getKeyValue("maxPlayerCount", maxPlayerCount);
    }

}
