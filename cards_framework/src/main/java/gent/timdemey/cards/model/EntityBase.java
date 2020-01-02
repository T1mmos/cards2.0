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
    
    @Override
    public final boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (!this.getClass().isAssignableFrom(obj.getClass()))
        {
            return false;
        }
        EntityBase other = (EntityBase) obj;
        
        if (!this.id.equals(other.id))
        {
            return false;
        }
        
        return true;
    }
    
    @Override
    public final int hashCode()
    {
        return id.hashCode();
    }
}
