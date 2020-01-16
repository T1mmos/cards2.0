package gent.timdemey.cards.model;

import java.util.UUID;

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
}
