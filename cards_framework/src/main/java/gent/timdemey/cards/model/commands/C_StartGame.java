package gent.timdemey.cards.model.commands;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.reflect.TypeToken;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ACommand;
import gent.timdemey.cards.readonlymodel.CommandType;
import gent.timdemey.cards.readonlymodel.IGameEventListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.ICommandExecutionService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_StartGame extends CommandBase
{
    private final Map<UUID, List<ReadOnlyCardStack>> playerStacks;
    
    public C_StartGame(Map<UUID, List<ReadOnlyCardStack>> playerStacks)
    {
        this.playerStacks = playerStacks;
    }
    
    @Override
    public CommandType getCommandType()
    {
        return CommandType.Gameplay;
    } 
    
    @Override
    public boolean canExecute() {
        return true;
    }
    
    @Override
    public void execute() {
        Context cf = getThreadContext();
        ContextType contextType = getContextType();
        
        
        
        if (contextType == ContextType.UI)
        {
            ReadOnlyCardGame game = new ReadOnlyCardGame(playerStacks);
            cf.getCardGameState().cardGame = game;
        }
        else if (contextType == ContextType.Client)
        {            
            // make a full copy of objects first, so they are not shared with UI layer
            try {
                C_StartGame commandCopy = (C_StartGame) Json.receive(this.getCommandEnvelope().serialize()).command;
                Map<UUID, List<ReadOnlyCardStack>> playerStacksCopy = commandCopy.playerStacks;
                ReadOnlyCardGame game = new ReadOnlyCardGame(playerStacksCopy);
                cf.getCardGameState().cardGame = game;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }            
            
            reschedule(ContextType.UI);
        }
        else 
        {          
            ReadOnlyCardGame game = new ReadOnlyCardGame(playerStacks);
            cf.getCardGameState().cardGame = game;
            
            ICommandExecutionService execServ = Services.get(ICommandExecutionService.class, ContextType.Server);
            
            execServ.srv_tcp_connpool.broadcast(cf.getPlayerIds(), getCommandEnvelope().serialize());
        }
    }
   
    
    @Override
    public boolean canUndo() {
        return false;
    }

    @Override
    public void undo() {
        throw new IllegalStateException("Cannot undo this command");
    }
}
