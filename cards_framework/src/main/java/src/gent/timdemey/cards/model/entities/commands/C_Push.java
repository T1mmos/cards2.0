package gent.timdemey.cards.model.entities.commands;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.cards.CardStack;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.utils.Debug;

public abstract class C_Push extends CommandBase
{
    private final UUID dstCardStackId;
    private final List<UUID> srcCardIds;
    
    protected C_Push(UUID dstCardStackId, List<UUID> srcCardIds)
    {
        this.dstCardStackId = dstCardStackId;
        this.srcCardIds = Collections.unmodifiableList(srcCardIds);        
    }
    
    @Override
    public final void execute(Context context, ContextType type, State state)
    {
        throw new UnsupportedOperationException("This command should not be executed directly, only the canExecute is important");
    }
    
    @Override
    public final boolean canExecute(Context context, ContextType type, State state)
    {
        CheckNotContext(type, ContextType.Client);
        
        CardStack dstCardStack = state.getCardGame().getCardStack(dstCardStackId);
        List<Card> srcCards = state.getCardGame().getCards().getOnly(srcCardIds);
        
        return canPush(dstCardStack, srcCards);
    }
    
    protected abstract boolean canPush(CardStack dstCardStack, List<Card> srcCards);
    
    @Override
    protected final boolean canUndo(Context context, ContextType type, State state)
    {
        return false;
    }
    
    @Override
    public final boolean isSyncable()
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
