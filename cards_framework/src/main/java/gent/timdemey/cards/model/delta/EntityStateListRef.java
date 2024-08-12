package gent.timdemey.cards.model.delta;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.entities.common.EntityList;
import gent.timdemey.cards.model.entities.common.EntityListExtensions;
import gent.timdemey.cards.model.entities.common.IEntityList;
import java.util.function.Function;

public class EntityStateListRef<X extends EntityBase> extends StateListRef<X> implements IEntityList<X>
{
    public EntityStateListRef(IChangeTracker changeTracker, Property<X> property, UUID entityId, List<X> wrappee)
    {
        super(changeTracker, property, entityId, wrappee);
    }

    @Override
    public X get(UUID id)
    {
        return EntityListExtensions.get(list, id);
    }

    @Override
    public List<UUID> getIds()
    {
        return EntityListExtensions.getIds(list);
    }

    @Override
    public EntityList<X> getExcept(UUID... excluded)
    {
        return EntityListExtensions.getExcept(list, excluded);
    }

    public List<UUID> getExceptUUID(UUID... excluded)
    {
        return EntityListExtensions.getExceptUUID(list, excluded);
    }

    public boolean contains(UUID id)
    {
        return EntityListExtensions.contains(list, id);
    }

    public EntityList<X> getOnly(List<UUID> included)
    {
        return EntityListExtensions.getOnly(list, included);
    }
    
    public X getFirst()
    {
        return EntityListExtensions.getFirst(list);
    }
    
    public X getLast()
    {
        return EntityListExtensions.getLast(list);
    }

    public X remove(UUID id)
    {
        return EntityListExtensions.remove(list, id);
    }

    @Override
    public <Y> List<Y> select(Function<X, Y> selector)
    {
        return EntityListExtensions.select(this, selector);   
    }
}
