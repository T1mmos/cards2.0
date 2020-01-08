package gent.timdemey.cards.readonlymodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.state.StateValueRef;

public class ReadOnlyEntityList<T extends ReadOnlyEntityBase<?>> extends ReadOnlyList<T> 
{

    public ReadOnlyEntityList(List<T> roList)
    {
        super(roList);
    }

    public T get(UUID id)
    {
        for (T t : wrappee)
        {
            if (t.getId().equals(id))
            {
                return t;
            }
        }
        throw new IllegalArgumentException("No entity found in this list for id=" + id);
    }

    public List<UUID> getIds()
    {
        List<UUID> ids = new ArrayList<>();
        for (T t : wrappee)
        {
            ids.add(t.getId());
        }
        return ids;
    }

    public List<T> getExcept(UUID... excluded)
    {
        List<UUID> exclIds = Arrays.asList(excluded);
        List<T> ts = new ArrayList<>();
        for (T t : wrappee)
        {
            if (!exclIds.contains(t.getId()))
            {
                ts.add(t);
            }
        }
        return ts;
    }

    public List<UUID> getExceptUUID(UUID... excluded)
    {
        List<UUID> exclIds = Arrays.asList(excluded);
        List<UUID> ts = new ArrayList<>();
        for (T t : wrappee)
        {
            if (!exclIds.contains(t.getId()))
            {
                ts.add(t.getId());
            }
        }
        return ts;
    }

    public List<T> getExcept(StateValueRef<UUID>... excluded)
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
        return wrappee.contains(id);
    }
}
