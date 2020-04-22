package gent.timdemey.cards.model.state;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.common.EntityBase;

public class EntityStateListRef<X extends EntityBase> extends StateListRef<X>
{
    public EntityStateListRef(Property<X> property, UUID entityId, List<X> wrappee)
    {
        super(property, entityId, wrappee);
    }

    public X get(UUID id)
    {
        for (X x : list)
        {
            if (x.id.equals(id))
            {
                return x;
            }
        }
        throw new IllegalArgumentException("No entity found in this list for id=" + id);
    }

    public List<UUID> getIds()
    {
        List<UUID> ids = new ArrayList<>();
        for (X x : list)
        {
            ids.add(x.id);
        }
        return ids;
    }

    public List<X> getExcept(UUID... excluded)
    {
        List<UUID> exclIds = Arrays.asList(excluded);
        List<X> xs = new ArrayList<>();
        for (X x : list)
        {
            if (!exclIds.contains(x.id))
            {
                xs.add(x);
            }
        }
        return xs;
    }

    public List<UUID> getExceptUUID(UUID... excluded)
    {
        List<UUID> exclIds = Arrays.asList(excluded);
        List<UUID> xs = new ArrayList<>();
        for (X x : list)
        {
            if (!exclIds.contains(x.id))
            {
                xs.add(x.id);
            }
        }
        return xs;
    }

    public boolean contains(UUID id)
    {
        return list.stream().anyMatch(x -> x.id.equals(id));
    }

    public List<X> getOnly(List<UUID> included)
    {
        List<X> xs = new ArrayList<>();
        for (X x : list)
        {
            if (included.contains(x.id))
            {
                xs.add(x);
            }
        }
        return xs;
    }
    
    public X getFirst()
    {
        if (list.isEmpty())
        {
            return null;
        }
        else
        {
            return list.get(0);
        }  
    }
    
    public X getLast()
    {
        if (list.isEmpty())
        {
            return null;
        }
        else
        {
            return list.get(list.size() - 1);
        }        
    }

    public X remove(UUID id)
    {
        X x = get(id);
        remove(x);
        return x;
    }
    
    public static <X extends EntityBase> EntityStateListRef<X> asReadOnly(List<X> subList)
    {
        return new EntityStateListRef<X>(null, null, Collections.unmodifiableList(subList));
    }

}
