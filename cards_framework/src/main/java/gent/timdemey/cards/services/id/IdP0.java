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
public final class IdP0 extends Id
{    
    IdP0 (String template)
    {
        super(template);
    }
    
    public UUID GetId()
    {
        UUID result = ToUUID();
        return result;
    }
}
