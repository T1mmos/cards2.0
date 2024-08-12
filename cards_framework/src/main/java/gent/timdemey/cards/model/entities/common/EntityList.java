package gent.timdemey.cards.model.entities.common;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 *
 * @author Timmos
 */
public class EntityList<X extends EntityBase> extends ArrayList<X> implements IEntityList<X> 
{
    public EntityList()
    {        
    }
    
    public static <E extends EntityBase> EntityList<E> from(List<E> other)
    {
        EntityList<E> list = new EntityList<>();
        list.addAll(other);
        return list;
    }
    
    public static <Y,E extends EntityBase> EntityList<E> from(List<Y> other, Function<Y, E> selector)
    {
        EntityList<E> list = new EntityList<>();
        for(Y y : other)
        {
            list.add(selector.apply(y));
        }
        return list;
    }
    
    @Override
    public X get(UUID id)
    {
        return EntityListExtensions.get(this, id);
    }

    @Override
    public List<UUID> getIds()
    {
        return EntityListExtensions.getIds(this);
    }

    @Override
    public EntityList<X> getExcept(UUID... excluded)
    {
        return EntityListExtensions.getExcept(this, excluded);
    }

    public List<UUID> getExceptUUID(UUID... excluded)
    {
        return EntityListExtensions.getExceptUUID(this, excluded);
    }

    public boolean contains(UUID id)
    {
        return EntityListExtensions.contains(this, id);
    }

    public EntityList<X> getOnly(List<UUID> included)
    {
        return EntityListExtensions.getOnly(this, included);
    }
    
    public X getFirst()
    {
        return EntityListExtensions.getFirst(this);
    }
    
    public X getLast()
    {
        return EntityListExtensions.getLast(this);
    }

    public X remove(UUID id)
    {
        return EntityListExtensions.remove(this, id);
    }

    @Override
    public <Y> List<Y> select(Function<X, Y> selector)
    {
        return EntityListExtensions.select(this, selector);        
    }
}
