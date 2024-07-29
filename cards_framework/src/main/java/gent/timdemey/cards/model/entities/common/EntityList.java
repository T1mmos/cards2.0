/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gent.timdemey.cards.model.entities.common;

import gent.timdemey.cards.model.entities.common.EntityBase;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Timmos
 */
public class EntityList<X extends EntityBase> extends ArrayList<X> implements IEntityList<X> 
{
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
    public List<X> getExcept(UUID... excluded)
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

    public List<X> getOnly(List<UUID> included)
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
}
