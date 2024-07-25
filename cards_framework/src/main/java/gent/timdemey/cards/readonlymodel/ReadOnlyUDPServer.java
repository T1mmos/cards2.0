package gent.timdemey.cards.readonlymodel;

import gent.timdemey.cards.common.Version;
import gent.timdemey.cards.model.entities.state.ServerTCP;
import gent.timdemey.cards.model.entities.state.ServerUDP;

public class ReadOnlyUDPServer extends ReadOnlyEntityBase<ServerUDP>
{
    protected ReadOnlyUDPServer(ServerUDP entity)
    {
        super(entity);
    }

    public ServerTCP getServer()
    {
        return entity.server;
    }

    public int getPlayerCount()
    {
        return entity.playerCount;
    }
    
    public int getMaxPlayerCount()
    {
        return entity.maxPlayerCount;
    }
    
    public Version getVersion()
    {
        return entity.version;
    }
}
