package gent.timdemey.cards.model.entities.state;

import gent.timdemey.cards.common.Version;
import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.entities.state.payload.P_ServerUDP;
import gent.timdemey.cards.utils.Debug;

public class ServerUDP extends EntityBase
{
    public final ServerTCP server;
    public final Version version;
    public final int playerCount;
    public final int maxPlayerCount;

    public ServerUDP(
        P_ServerUDP parameters)
    {
        super(parameters);
        
        this.server = parameters.server;
        this.version = parameters.version;
        this.playerCount = parameters.playerCount;
        this.maxPlayerCount = parameters.maxPlayerCount;
    }
    
    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("server", server) + Debug.getKeyValue("version", version)
             + Debug.getKeyValue("playerCount", playerCount) + Debug.getKeyValue("maxPlayerCount", maxPlayerCount);
    }

}
