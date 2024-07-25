/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gent.timdemey.cards.model.entities.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Timmos
 */
public class EntityListExtensions 
{
    
    public static <X extends EntityBase> X get(List<X> list, UUID id)
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

    public static <X extends EntityBase> List<UUID> getIds(List<X> list)
    {
        List<UUID> ids = new ArrayList<>();
        for (X x : list)
        {
            ids.add(x.id);
        }
        return ids;
    }

    public static <X extends EntityBase> List<X> getExcept(List<X> list, UUID... excluded)
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

    public static <X extends EntityBase> List<UUID> getExceptUUID(List<X> list, UUID... excluded)
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

    public static <X extends EntityBase> boolean contains(List<X> list, UUID id)
    {
        return list.stream().anyMatch(x -> x.id.equals(id));
    }

    public static <X extends EntityBase> List<X> getOnly(List<X> list, List<UUID> included)
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
    
    public static <X extends EntityBase> X getFirst(List<X> list)
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
    
    public static <X extends EntityBase> X getLast(List<X> list)
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

    public static <X extends EntityBase> X remove(List<X> list, UUID id)
    {
        X x = get(list, id);
        list.remove(x);
        return x;
    }
}
