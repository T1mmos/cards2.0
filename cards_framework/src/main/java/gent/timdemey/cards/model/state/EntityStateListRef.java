package gent.timdemey.cards.model.state;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.EntityBase;

public class EntityStateListRef<X extends EntityBase> extends StateListRef<X> 
{
    public EntityStateListRef(List<X> wrappee)
    {
        super(wrappee);
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

    public List<X> getExcept(StateValueRef<UUID>... excluded)
    {
        UUID[] exclIds = new UUID[excluded.length];
        for (int i = 0; i < excluded.length; i++)
        {
            StateValueRef<UUID> ref = excluded[i];
            UUID id = ref.get();
            exclIds[i] = id;
        }
        return getExcept(exclIds);
    }

    public boolean contains(UUID id)
    {
        return list.stream().anyMatch(x -> x.id.equals(id));
    }

}
