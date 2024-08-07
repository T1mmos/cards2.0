package gent.timdemey.cards.model.entities.common;

import java.util.UUID;

import gent.timdemey.cards.utils.Debug;

public abstract class EntityBase
{
    public final UUID id;

    protected EntityBase(UUID id)
    {
        if (id == null)
        {
            throw new IllegalArgumentException("id");
        }
        
        this.id = id;
    }

    protected EntityBase(PayloadBase pl)
    {
        if (pl == null)
        {
            throw new NullPointerException("pl");
        }
        if (pl.id == null)
        {
            throw new IllegalArgumentException("No id was specified for this Entity in the given PayloadBase");
        }
        
        this.id = pl.id;
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
    
    @Override
    public final String toString()
    {
        return getClass().getSimpleName() +
              "\n{" + 
              Debug.getKeyValue("id", id) +              
              toDebugString() + 
              "\n}";
    }
    
    public abstract String toDebugString();
}
