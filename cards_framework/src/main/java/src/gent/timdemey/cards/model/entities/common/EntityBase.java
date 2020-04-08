package gent.timdemey.cards.model.entities.common;

import java.util.UUID;

public abstract class EntityBase
{
    public final UUID id;

    protected EntityBase()
    {
        this.id = UUID.randomUUID();
    }

    protected EntityBase(PayloadBase pl)
    {
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
        return getClass().getSimpleName() + " [ " + toDebugString() + " ] ";
    }
    
    public abstract String toDebugString();
}
