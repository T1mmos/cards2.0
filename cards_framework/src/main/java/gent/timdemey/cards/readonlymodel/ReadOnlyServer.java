package gent.timdemey.cards.readonlymodel;

import gent.timdemey.cards.model.entities.state.ServerTCP;

public class ReadOnlyServer extends ReadOnlyEntityBase<ServerTCP>
{
    protected ReadOnlyServer(ServerTCP entity)
    {
        super(entity);
    }
}
