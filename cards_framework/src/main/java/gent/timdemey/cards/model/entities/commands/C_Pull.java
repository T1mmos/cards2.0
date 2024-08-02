package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;

import gent.timdemey.cards.model.entities.state.Card;
import gent.timdemey.cards.model.entities.state.CardStack;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_Pull;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.utils.Debug;

public abstract class C_Pull extends CommandBase
{
    private final UUID srcCardStackId;
    private final UUID srcCardId;
    
    public C_Pull(
        IContextService contextService,
        State state,
        P_Pull parameters)
    {
        super(contextService, state, parameters);
        
        this.srcCardStackId = parameters.srcCardStackId;
        this.srcCardId = parameters.srcCardId;
    }
    
    @Override
    public final CanExecuteResponse canExecute(Context context, ContextType type)
    {        
        if (_State.getGameState() != GameState.Started)
        {
            return CanExecuteResponse.no("GameState should be Started but is: " + _State.getGameState());
        }
        
        CardStack srcCardStack = _State.getCardGame().getCardStack(srcCardStackId);
        Card srcCard = _State.getCardGame().getCards().get(srcCardId);
                
        return canPull(srcCardStack, srcCard);
    }
    
    /**
     * Override this method to implement plugin/game specific business rules.
     * @param srcCardStack
     * @param srcCard
     * @return
     */
    protected CanExecuteResponse canPull(CardStack srcCardStack, Card srcCard)
    {
        return CanExecuteResponse.yes();
    }

    
    @Override
    public final void execute(Context context, ContextType type)
    {
        throw new UnsupportedOperationException("This command should not be executed directly, only the canExecute is important");
    }
    
    @Override
    public final boolean canUndo(Context context, ContextType type)
    {
        return false;
    }
    
    @Override
    public final CommandType getCommandType()
    {
        return CommandType.SYNCED;
    }
    
    @Override
    public final void undo(Context context, ContextType type)
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

