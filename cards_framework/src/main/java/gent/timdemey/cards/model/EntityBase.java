package gent.timdemey.cards.model;

import java.util.UUID;

public abstract class EntityBase
{
    public final UUID id;
    
    protected EntityBase()
    {
        this(UUID.randomUUID());
    }
    
    protected EntityBase(UUID id)
    {
        this.id = id;
    }
}
