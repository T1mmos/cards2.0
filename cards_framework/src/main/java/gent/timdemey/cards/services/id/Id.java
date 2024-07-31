/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gent.timdemey.cards.services.id;

import java.util.UUID;

/**
 *
 * @author Timmos
 */
public class Id
{
    protected final String _Template;
    
    public Id(String template)
    {
        this._Template = template;
    }
    
    protected UUID ToUUID(String ... args)
    {
        String instance = String.format(_Template, (Object[]) args);
        UUID id = UUID.nameUUIDFromBytes(instance.getBytes());
        return id;
    }
}
