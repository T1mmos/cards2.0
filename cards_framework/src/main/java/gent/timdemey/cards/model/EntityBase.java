package gent.timdemey.cards.model;

import java.util.UUID;

import gent.timdemey.cards.model.state.State;

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
