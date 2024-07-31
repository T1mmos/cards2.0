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
public final class IdWrapperP2<P, P1, P2>
{
    private final IdP2_2<P1, P2> _InnerId;
    private final Function<P, P1> _P1Mapper;
    private final Function<P, P2> _P2Mapper;
    
    IdWrapperP2 (IdP2_2<P1, P2> innerId, Function<P, P1> mapper1, Function<P, P2> mapper2)
    {
        this._InnerId = innerId;
        this._P1Mapper = mapper1;
        this._P2Mapper = mapper2;
    }
    
    public UUID GetId(P param)
    {
        P1 p1 = _P1Mapper.apply(param);
        P2 p2 = _P2Mapper.apply(param);
        return _InnerId.GetId(p1, p2);
    }
}
