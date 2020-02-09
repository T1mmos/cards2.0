package gent.timdemey.cards.model.other;

import java.util.UUID;

import gent.timdemey.cards.model.EntityBase;

public class Player extends EntityBase
{
    public final String name;

    public Player(String name)
    {
        super();
        this.name = name;
    }

    public Player(UUID id, String name)
    {
        super(id);
        this.name = name;
    }

    @Override
    public String toDebugString()
    {
        return name;
    }
}
