package gent.timdemey.cards.model.commands;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.cards.CardStack;
import gent.timdemey.cards.model.entities.cards.Value;
import gent.timdemey.cards.model.entities.commands.C_Push;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.boot.SolitaireCardStackType;

public class C_SolPush extends C_Push
{
    public C_SolPush(UUID dstCardStackId, List<UUID> srcCardIds)
    {
        super(dstCardStackId, srcCardIds);
    }

    @Override
    protected boolean canPush(CardStack dstCardStack, List<Card> srcCards, State state)
    {
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

}
