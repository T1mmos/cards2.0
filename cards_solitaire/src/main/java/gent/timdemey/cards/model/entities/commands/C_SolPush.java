package gent.timdemey.cards.model.entities.commands;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.state.Card;
import gent.timdemey.cards.model.entities.state.CardStack;
import gent.timdemey.cards.model.entities.state.CardValue;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.contract.descriptors.SolitaireComponentTypes;

public class C_SolPush extends C_Push
{
    public C_SolPush(UUID dstCardStackId, List<UUID> srcCardIds)
    {
        super(dstCardStackId, srcCardIds);
    }

    @Override
    protected CanExecuteResponse canPush(CardStack dstCardStack, List<Card> srcCards, State state)
    {
        String dstType = dstCardStack.cardStackType;

        if (dstType.equals(SolitaireComponentTypes.MIDDLE))
        {
            if (dstCardStack.getCards().isEmpty() && srcCards.get(0).value == CardValue.V_K
                    || !dstCardStack.getCards().isEmpty()
                            && dstCardStack.getHighestCard().suit.getColor() != srcCards.get(0).suit.getColor()
                            && dstCardStack.getHighestCard().value
                                    .getOrderAtoK() == srcCards.get(0).value.getOrderAtoK() + 1)
            {
                return CanExecuteResponse.yes();
            }
        }
        else if (dstType.equals(SolitaireComponentTypes.LAYDOWN))
        {
            if (srcCards.size() == 1)
            {
                if (dstCardStack.getCards().isEmpty() && srcCards.get(0).value == CardValue.V_A
                        || !dstCardStack.getCards().isEmpty()
                                && dstCardStack.getHighestCard().suit == srcCards.get(0).suit
                                && dstCardStack.getHighestCard().value.getOrderAtoK() + 1 == srcCards.get(0).value
                                        .getOrderAtoK())
                {
                    return CanExecuteResponse.yes();
                }
            }

        }

        return CanExecuteResponse.no("Not a valid push action");
    }

}
