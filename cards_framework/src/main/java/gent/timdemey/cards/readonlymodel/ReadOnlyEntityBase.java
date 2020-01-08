package gent.timdemey.cards.readonlymodel;

import java.util.UUID;

import gent.timdemey.cards.model.EntityBase;

public abstract class ReadOnlyEntityBase<T extends EntityBase>
{
    protected final T entity;

    protected ReadOnlyEntityBase(T entity)
    {
        this.entity = entity;
    }
    
    public final UUID getId ()
    {
        return entity.id;
    }
    
    @Override
    public final boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof ReadOnlyEntityBase))
        {
            return false;
        }
        
        ReadOnlyEntityBase<?> other = (ReadOnlyEntityBase<?>) obj;
        return entity.equals(other.entity);
    }
    
    @Override
    public final int hashCode()
    {
        return entity.hashCode();
    }
    
    @Override
    public final String toString() {
        return "[R/O] " + entity.toString(); 
    }
}
