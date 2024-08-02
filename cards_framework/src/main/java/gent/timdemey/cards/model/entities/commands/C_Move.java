package gent.timdemey.cards.model.entities.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.state.Card;
import gent.timdemey.cards.model.entities.state.CardGame;
import gent.timdemey.cards.model.entities.state.CardStack;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_Move;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.utils.Debug;

public class C_Move extends CommandBase
{
    public final UUID srcCardStackId;
    public final UUID dstCardStackId;
    public final UUID cardId;
    protected List<Card> transferCards;

    public C_Move(
        IContextService contextService, State state,            
        P_Move parameters)
    {
        super(contextService, state, parameters);
        
        this.srcCardStackId = parameters.srcCardStackId;
        this.dstCardStackId = parameters.dstCardStackId;
        this.cardId = parameters.cardId;
    }

    /**
     * Override this method to implement plugin/game specific business rules.
     * 
     * @param dstCardStack
     * @param srcCards
     * @return
     */
    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type)
    {
        if (_State.getGameState() != GameState.Started)
        {
            return CanExecuteResponse.no("GameState should be Started but is: " + _State.getGameState());
        }
        return CanExecuteResponse.yes();
    }

    /**
     * Override this method to implement plugin/game specific business rules.
     * 
     * @param dstCardStack
     * @param srcCards
     * @return
     */
    @Override
    protected void execute(Context context, ContextType type)
    {
        CardGame cardGame = _State.getCardGame();
        CardStack srcCardStack = cardGame.getCardStack(srcCardStackId);
        CardStack dstCardStack = cardGame.getCardStack(dstCardStackId);

        if (transferCards == null)
        {
            List<Card> cards = srcCardStack.getCards();
            Card card = srcCardStack.getCards().get(cardId);
            transferCards = new ArrayList<>(cards.subList(cards.indexOf(card), cards.size()));
        }

        srcCardStack.removeAll(transferCards);
        dstCardStack.addAll(transferCards);
        transferCards.forEach(card -> card.cardStack = dstCardStack);
    }

    public final boolean isSyncable()
    {
        return true;
    }

    @Override
    protected void undo(Context context, ContextType type)
    {
        CardGame cardGame = _State.getCardGame();
        CardStack srcCardStack = cardGame.getCardStacks().get(srcCardStackId);
        CardStack dstCardStack = cardGame.getCardStacks().get(dstCardStackId);

        dstCardStack.getCards().removeAll(transferCards);
        srcCardStack.addAll(transferCards);
        transferCards.forEach(card -> card.cardStack = srcCardStack);
    }

    @Override
    protected boolean canUndo(Context context, ContextType type)
    {
        return true;
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("srcCardStackId", srcCardStackId) + Debug.getKeyValue("dstCardStackId", dstCardStackId)
                + Debug.getKeyValue("cardId", cardId);
    }
}
