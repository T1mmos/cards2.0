package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.di.Container;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.state.Card;
import gent.timdemey.cards.model.entities.state.CardStack;
import gent.timdemey.cards.model.entities.state.CardValue;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_Push;
import gent.timdemey.cards.services.cardgame.SolShowCardStackType;

public class C_SolShowPush extends C_Push
{
    public C_SolShowPush(
        Container container,
        P_Push parameters)
    {
        super(container, parameters);
    }

    @Override
    protected CanExecuteResponse canPush(CardStack dstCardStack, List<Card> srcCards)
    {
        if (dstCardStack.cardStackType.equals(SolShowCardStackType.DEPOT)
                || dstCardStack.cardStackType.equals(SolShowCardStackType.TURNOVER)
                || dstCardStack.cardStackType.equals(SolShowCardStackType.SPECIAL))
        {
            return CanExecuteResponse.no("Cannot push onto a " + dstCardStack.cardStackType + " stack");
        }

        if (dstCardStack.cardStackType.equals(SolShowCardStackType.MIDDLE))
        {
            UUID playerId = creatorId;
            if (!playerId.equals(_State.getServerId())
                    && !playerId.equals(_State.getCardGame().getPlayerId(dstCardStack)))
            {
                return CanExecuteResponse.no("Can only push onto your own MIDDLE stacks");
            }

            // in solitaire showdown you are allowed to put any card on an empty stack,
            // not just a king
            if (dstCardStack.cards.isEmpty())
            {
                return CanExecuteResponse.yes();
            }

            Card dstCard = dstCardStack.getHighestCard();
            Card srcCard = srcCards.get(0);

            if (dstCard.suit.getColor() == srcCard.suit.getColor())
            {
                return CanExecuteResponse.no("Suit color must differ");
            }

            int value_src = srcCard.value.getOrderAtoK();
            int value_dst = dstCard.value.getOrderAtoK();
            if (value_dst != value_src + 1)
            {
                return CanExecuteResponse.no("Value of destination card must be 1 more than source card");
            }

            return CanExecuteResponse.yes();
        }
        else if (dstCardStack.cardStackType.equals(SolShowCardStackType.LAYDOWN))
        {
            if (srcCards.size() != 1)
            {
                return CanExecuteResponse.no("Can only push 1 card at a time");
            }
            Card srcCard = srcCards.get(0);

            if (!srcCard.visibleRef.get())
            {
                return CanExecuteResponse.no("The card must be visible");
            }

            if (srcCard.value == CardValue.V_A)
            {
                if (!dstCardStack.cards.isEmpty())
                {
                    return CanExecuteResponse.no("An ace requires an empty stack");
                }

                return CanExecuteResponse.yes();
            }
            else
            {
                if (dstCardStack.cards.isEmpty())
                {
                    return CanExecuteResponse.no("Any card not an ace requires a non-empty destination stack");
                }
                Card dstCard = dstCardStack.getHighestCard();

                if (dstCard.suit != srcCard.suit)
                {
                    return CanExecuteResponse.no("The suit must match");
                }

                int value_src = srcCard.value.getOrderAtoK();
                int value_dst = dstCard.value.getOrderAtoK();
                if (value_dst + 1 != value_src)
                {
                    return CanExecuteResponse.no("Value of source card must be 1 more than destination card");
                }

                return CanExecuteResponse.yes();
            }
        }

        throw new UnsupportedOperationException(
                "No such SolShowCardStackType supported: " + dstCardStack.cardStackType);
    }
}
