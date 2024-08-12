/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gent.timdemey.cards.mock;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.CommandFactory;
import gent.timdemey.cards.model.entities.commands.game.C_Pull;
import gent.timdemey.cards.model.entities.commands.game.C_Push;
import gent.timdemey.cards.model.entities.commands.game.C_Use;
import gent.timdemey.cards.model.entities.commands.game.P_Pull;
import gent.timdemey.cards.model.entities.commands.game.P_Push;
import gent.timdemey.cards.model.entities.commands.game.P_Use;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Timmos
 */
public class MockCommandFactory extends CommandFactory
{

    public MockCommandFactory (Container container)
    {
        super(container);
    }
    
    @Override
    public C_Push CreatePush(UUID dstCardStackId, List<UUID> srcCardIds)
    {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public C_Push CreatePush(P_Push parameters)
    {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public C_Use CreateUse(UUID initiatorCardStackId, UUID initiatorCardId)
    {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public C_Use CreateUse(P_Use parameters)
    {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public C_Pull CreatePull(UUID cardStackId, UUID cardId)
    {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public C_Pull CreatePull(P_Pull parameters)
    {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
