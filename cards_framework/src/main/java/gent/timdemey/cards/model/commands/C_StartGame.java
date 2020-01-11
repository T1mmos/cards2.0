package gent.timdemey.cards.model.commands;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.cards.CardGame;
import gent.timdemey.cards.model.cards.CardStack;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.ICommandExecutionService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_StartGame extends CommandBase
{
    private final Map<UUID, List<CardStack>> playerStacks;
    
    public C_StartGame(Map<UUID, List<CardStack>> playerStacks)
    {
        this.playerStacks = playerStacks;
    }
    
    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
    	return state.getCardGame() == null;
    }
    
    @Override
    protected void execute(Context context, ContextType type, State state)
    {    
        if (type == ContextType.UI)
        {
            CardGame game = new CardGame(playerStacks);
            state.setCardGame(game);
        }
        else if (type == ContextType.Client)
        {            
            // make a full copy of objects first, so they are not shared with UI layer            
        	String json = CommandDtoMapper.toJson(this);
        	C_StartGame commandCopy = (C_StartGame) CommandDtoMapper.toCommand(json);
            Map<UUID, List<CardStack>> playerStacksCopy = commandCopy.playerStacks;
            CardGame game = new CardGame(playerStacksCopy);
            state.setCardGame(game);
                
            reschedule(ContextType.UI);
        }
        else 
        {          
            CardGame game = new CardGame(playerStacks);
            state.setCardGame(game);
            
            ICommandExecutionService execServ = Services.get(ICommandExecutionService.class, ContextType.Server);
            String json = CommandDtoMapper.toJson(this);
            List<UUID> remoteIds = state.getPlayers().getExceptUUID(state.getServerId());
            state.getTcpConnectionPool().broadcast(remoteIds, json);
        }
    }
}
