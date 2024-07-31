/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.payload.P_Move;
import gent.timdemey.cards.model.entities.commands.payload.P_Pull;
import gent.timdemey.cards.model.entities.commands.payload.P_Push;
import gent.timdemey.cards.model.entities.commands.payload.P_Use;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Timmos
 */
public class SolCommandFactory extends CommandFactory
{    
    public SolCommandFactory(Container container)
    {
        super(container);
    }

    @Override
    public C_SolMove CreateMove(UUID srcCardStackId, UUID dstCardStackId, UUID cardId)
    {
        P_Move p = new P_Move();
        
        p.id = UUID.randomUUID();
        p.srcCardStackId = srcCardStackId;
        p.dstCardStackId = dstCardStackId;
        p.cardId = cardId;
        
        return CreateMove(p);
    }

    @Override
    public C_SolMove CreateMove(P_Move parameters)
    {
        return DICreate(C_SolMove.class, P_Move.class, parameters);
    }

    @Override
    public C_Push CreatePush(UUID dstCardStackId, List<UUID> srcCardIds)
    {
        P_Push p = new P_Push();
        
        p.id = UUID.randomUUID();
        p.dstCardStackId = dstCardStackId;
        p.srcCardIds = srcCardIds;
        
        return CreatePush(p);
    }

    @Override
    public C_Push CreatePush(P_Push parameters)
    {
        return DICreate(C_SolPush.class, P_Push.class, parameters);
    }

    @Override
    public C_SolUse CreateUse(UUID initiatorCardStackId, UUID initiatorCardId)
    {
        P_Use p = new P_Use();
        
        p.id = UUID.randomUUID();
        p.initiatorStackId = initiatorCardStackId;
        p.initiatorCardId = initiatorCardId;
        
        return CreateUse(p);
    }
    
    @Override
    public C_SolUse CreateUse(P_Use parameters)
    {
        return DICreate(C_SolUse.class, P_Use.class, parameters);
    }
    
    @Override
    public C_SolPull CreatePull(UUID cardStackId, UUID cardId)
    {
        P_Pull p = new P_Pull();
        
        p.srcCardStackId = cardStackId;
        p.srcCardId = cardId;
        
        return CreatePull(p);
    }

    @Override
    public C_SolPull CreatePull(P_Pull parameters)
    {
        return DICreate(C_SolPull.class, P_Pull.class, parameters);
    }


}
