package gent.timdemey.cards.model.entities.game;

import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.entities.game.payload.P_Player;
import gent.timdemey.cards.utils.Debug;

public class Player extends EntityBase
{
    public final String name;

    public Player(String name)
    {
        super();
        this.name = name;
    }

    public Player(P_Player pl)
    {
        super(pl);
        this.name = pl.name;
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("name", name);
    }
}
