package gent.timdemey.cards.model.commands;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.entities.SolitaireCardStackType;
import gent.timdemey.cards.model.cards.Card;
import gent.timdemey.cards.model.cards.CardStack;
import gent.timdemey.cards.model.cards.Value;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_Push extends CommandBase
{
    public final UUID dstCardStackId;
    public final List<UUID> srcCardIds;

    public C_Push(UUID dstCardStackId, List<UUID> srcCardIds)
    {
        this.dstCardStackId = dstCardStackId;
        this.srcCardIds = Collections.unmodifiableList(srcCardIds);
    }

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        CardStack dstCardStack = state.getCardGame().getCardStack(dstCardStackId);
        List<Card> srcCards = state.getCardGame().getCards().getOnly(srcCardIds);

        String dstType = dstCardStack.cardStackType;

        if(dstType.equals(SolitaireCardStackType.MIDDLE))
        {
            if(dstCardStack.getCards().isEmpty() && srcCards.get(0).value == Value.V_K
                    || !dstCardStack.getCards().isEmpty()
                            && dstCardStack.getHighestCard().suit.getColor() != srcCards.get(0).suit.getColor()
                            && dstCardStack.getHighestCard().value
                                    .getOrderAtoK() == srcCards.get(0).value.getOrderAtoK() + 1)
            {
                return true;
            }
        }
        else if(dstType.equals(SolitaireCardStackType.LAYDOWN))
        {
            if(srcCards.size() == 1)
            {
                if(dstCardStack.getCards().isEmpty() && srcCards.get(0).value == Value.V_A
                        || !dstCardStack.getCards().isEmpty()
                                && dstCardStack.getHighestCard().suit == srcCards.get(0).suit
                                && dstCardStack.getHighestCard().value.getOrderAtoK() + 1 == srcCards.get(0).value
                                        .getOrderAtoK())
                {
                    return true;
                }
            }

        }
        return false;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        throw new UnsupportedOperationException("Use a C_Move command instead!");
    }

}
