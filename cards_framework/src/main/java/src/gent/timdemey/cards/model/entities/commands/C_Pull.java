package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;

import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.cards.CardStack;
import gent.timdemey.cards.model.entities.game.GameState;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.utils.Debug;

public abstract class C_Pull extends CommandBase
{
    private final UUID srcCardStackId;
    private final UUID srcCardId;
    
    protected C_Pull(UUID srcCardStackId, UUID srcCardId)
    {
        this.srcCardStackId = srcCardStackId;
        this.srcCardId = srcCardId;
    }
    
    @Override
    public final CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {        
        if (state.getGameState() != GameState.Started)
        {
            return CanExecuteResponse.no("GameState should be Started but is: " + state.getGameState());
        }
        
        CardStack srcCardStack = state.getCardGame().getCardStack(srcCardStackId);
        Card srcCard = state.getCardGame().getCards().get(srcCardId);
                
        return canPull(srcCardStack, srcCard, state);
    }
    
    /**
     * Override this method to implement plugin/game specific business rules.
     * @param srcCardStack
     * @param srcCard
     * @return
     */
    protected CanExecuteResponse canPull(CardStack srcCardStack, Card srcCard, State state)
    {
        return CanExecuteResponse.yes();
    }

    
    @Override
    public final void preExecute(Context context, ContextType type, State state)
    {
        throw new UnsupportedOperationException("This command should not be executed directly, only the canExecute is important");
    }
    
    @Override
    public final boolean canUndo(Context context, ContextType type, State state)
    {
        return false;
    }
    
    @Override
    public final CommandType getCommandType()
    {
        return CommandType.SYNCED;
    }
    
    @Override
    public final void undo(Context context, ContextType type, State state)
    {
        throw new UnsupportedOperationException("This command should not be executed directly");
    }
    
    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("srcCardStackId", srcCardStackId) + 
               Debug.getKeyValue("srcCardId", srcCardId);
    }
}

