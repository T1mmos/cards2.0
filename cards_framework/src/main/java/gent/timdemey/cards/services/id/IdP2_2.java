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
public final class IdP2_2<P1, P2> extends Id
{
    private final Function<P1, String> _P1Mapper;
    private final Function<P2, String> _P2Mapper;
    
    IdP2_2 (String template, Function<P1, String> mapper1, Function<P2, String> mapper2)
    {
        super(template);
        this._P1Mapper = mapper1;
        this._P2Mapper = mapper2;
    }
    
    public UUID GetId(P1 param1, P2 param2)
    {
        String p1Value = _P1Mapper.apply(param1);
        String p2Value = _P2Mapper.apply(param2);
        UUID result = ToUUID(p1Value, p2Value);
        return result;
    }
}
