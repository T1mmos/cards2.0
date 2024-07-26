package gent.timdemey.cards.model.entities.commands;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.state.Card;
import gent.timdemey.cards.model.entities.state.CardStack;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.utils.Debug;

public abstract class C_Push extends CommandBase
{
    private final UUID dstCardStackId;
    private final List<UUID> srcCardIds;
    
    protected C_Push(
        IContextService contextService, 
        UUID id, UUID dstCardStackId, List<UUID> srcCardIds)
    {
        super(contextService, id);
        this.dstCardStackId = dstCardStackId;
        this.srcCardIds = Collections.unmodifiableList(srcCardIds);        
    }
    
    @Override
    public final CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {        
        if (state.getGameState() != GameState.Started)
        {
            return CanExecuteResponse.no("GameState should be Started but is: " + state.getGameState());
        }
        
        CardStack dstCardStack = state.getCardGame().getCardStack(dstCardStackId);
        List<Card> srcCards = state.getCardGame().getCards().getOnly(srcCardIds);
        
        return canPush(dstCardStack, srcCards, state);
    }
    
    /**
     * Override this method to implement plugin/game specific business rules.
     * @param dstCardStack
     * @param srcCards
     * @return
     */
    protected CanExecuteResponse canPush(CardStack dstCardStack, List<Card> srcCards, State state)
    {
        return CanExecuteResponse.yes();
    }
    
    @Override
    public final void execute(Context context, ContextType type, State state)
    {
        throw new UnsupportedOperationException("This command should not be executed directly, only the canExecute is important");
    }
    
    @Override
    protected final boolean canUndo(Context context, ContextType type, State state)
    {
        return false;
    }
    
    @Override
    protected final void undo(Context context, ContextType type, State state)
    {
        throw new UnsupportedOperationException("This command should not be executed directly");
    }
    
    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("dstCardStackId", dstCardStackId) +
               Debug.listAny("srcCardIds", srcCardIds);
    }
}
