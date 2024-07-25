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
public interface IEntityList<X extends EntityBase>
{    
    public X get(UUID id);

    public List<UUID> getIds();

    public List<X> getExcept(UUID... excluded);

    public List<UUID> getExceptUUID(UUID... excluded);

    public boolean contains(UUID id);

    public List<X> getOnly(List<UUID> included);
    
    public X getFirst();
    
    public X getLast();

    public X remove(UUID id);
}
