package gent.timdemey.cards.readonlymodel;

import gent.timdemey.cards.common.Version;
import gent.timdemey.cards.model.entities.game.Server;
import gent.timdemey.cards.model.entities.game.UDPServer;

public class ReadOnlyUDPServer extends ReadOnlyEntityBase<UDPServer>
{
    protected ReadOnlyUDPServer(UDPServer entity)
    {
        super(entity);
    }

    public Server getServer()
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
