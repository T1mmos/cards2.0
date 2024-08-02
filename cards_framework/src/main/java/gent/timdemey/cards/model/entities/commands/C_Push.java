package gent.timdemey.cards.model.entities.commands;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.state.Card;
import gent.timdemey.cards.model.entities.state.CardStack;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_Push;
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
    
    public C_Push(
        IContextService contextService, 
        State state,
        P_Push parameters)
    {
        super(contextService, state, parameters);
        this.dstCardStackId = parameters.dstCardStackId;
        this.srcCardIds = Collections.unmodifiableList(parameters.srcCardIds);        
    }
    
    @Override
    public final CanExecuteResponse canExecute(Context context, ContextType type)
    {        
        if (_State.getGameState() != GameState.Started)
        {
            return CanExecuteResponse.no("GameState should be Started but is: " + _State.getGameState());
        }
        
        CardStack dstCardStack = _State.getCardGame().getCardStack(dstCardStackId);
        List<Card> srcCards = _State.getCardGame().getCards().getOnly(srcCardIds);
        
        return canPush(dstCardStack, srcCards);
    }
    
    /**
     * Override this method to implement plugin/game specific business rules.
     * @param dstCardStack
     * @param srcCards
     * @return
     */
    protected CanExecuteResponse canPush(CardStack dstCardStack, List<Card> srcCards)
    {
        return CanExecuteResponse.yes();
    }
    
    @Override
    public final void execute(Context context, ContextType type)
    {
        throw new UnsupportedOperationException("This command should not be executed directly, only the canExecute is important");
    }
    
    @Override
    protected final boolean canUndo(Context context, ContextType type)
    {
        return false;
    }
    
    @Override
    protected final void undo(Context context, ContextType type)
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
