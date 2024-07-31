/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gent.timdemey.cards.services.id;

import java.util.UUID;
import java.util.function.Function;

/**
 *
 * @author Timmos
 */
public final class IdP1<P> extends Id
{
    private final Function<P, String> _P1Mapper;
    
    IdP1 (String template, Function<P, String> mapper)
    {
        super(template);
        this._P1Mapper = mapper;
    }
    
    public UUID GetId(P param1)
    {
        String p1Value = _P1Mapper.apply(param1);
        UUID result = ToUUID(p1Value);
        return result;
    }
}
