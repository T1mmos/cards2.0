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
public final class IdWrapperP1<P, P1>
{
    private final IdP1<P1> _InnerId;
    private final Function<P, P1> _P1Mapper;
    
    IdWrapperP1 (IdP1<P1> innerId, Function<P, P1> mapper1)
    {
        this._InnerId = innerId;
        this._P1Mapper = mapper1;
    }
    
    public UUID GetId(P param)
    {
        P1 p1 = _P1Mapper.apply(param);
        return _InnerId.GetId(p1);
    }
}
