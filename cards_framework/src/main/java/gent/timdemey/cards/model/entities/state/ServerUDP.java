package gent.timdemey.cards.model.entities.state;

import gent.timdemey.cards.common.Version;
import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.utils.Debug;
import java.util.UUID;

public class ServerUDP extends EntityBase
{
    public final ServerTCP server;
    public final Version version;
    public final int playerCount;
    public final int maxPlayerCount;

    ServerUDP(UUID id, ServerTCP server, Version version, int playerCount, int maxPlayerCount)
    {
        super(id);
        
        this.server = server;
        this.version = version;
        this.playerCount = playerCount;
        this.maxPlayerCount = maxPlayerCount;
    }
    
    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("server", server) + Debug.getKeyValue("version", version)
             + Debug.getKeyValue("playerCount", playerCount) + Debug.getKeyValue("maxPlayerCount", maxPlayerCount);
    }

}
