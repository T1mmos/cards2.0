package gent.timdemey.cards.model.entities.commands.game;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.state.Card;
import gent.timdemey.cards.model.entities.state.CardStack;
import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.utils.Debug;

public abstract class C_Push extends CommandBase<P_Push>
{
    private final UUID dstCardStackId;
    private final List<UUID> srcCardIds;
    
    public C_Push(
        Container container,
        P_Push parameters)
    {
        super(container, parameters);
        this.dstCardStackId = parameters.dstCardStackId;
        this.srcCardIds = Collections.unmodifiableList(parameters.srcCardIds);        
    }
    
    @Override
    public final CanExecuteResponse canExecute()
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
    public final void execute()
    {
        throw new UnsupportedOperationException("This command should not be executed directly, only the canExecute is important");
    }
    
    @Override
    public final boolean canUndo()
    {
        return false;
    }
    
    @Override
    public final void undo()
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
