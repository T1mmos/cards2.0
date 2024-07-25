package gent.timdemey.cards.model.entities.commands;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.state.Card;
import gent.timdemey.cards.model.entities.state.CardStack;
import gent.timdemey.cards.model.entities.state.SuitColor;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.cardgame.SolShowCardStackType;

public class C_SolShowPull extends C_Pull
{
    public C_SolShowPull(UUID srcCardStackId, UUID cardId)
    {
        super(srcCardStackId, cardId);
    }

    @Override
    protected CanExecuteResponse canPull(CardStack srcCardStack, Card srcCard, State state)
    {
        if (srcCardStack.cardStackType.equals(SolShowCardStackType.DEPOT)
                || srcCardStack.cardStackType.equals(SolShowCardStackType.LAYDOWN))
        {
            return CanExecuteResponse.no("Cannot move from DEPOT to LAYDOWN");
        }

        UUID srcPlayerId = getSourceId();
        if (!srcPlayerId.equals(state.getServerId())
                && !srcPlayerId.equals(state.getCardGame().getPlayerId(srcCardStack)))
        {
            return CanExecuteResponse.no("Can only pull from own stacks");
        }

        if (srcCardStack.cardStackType.equals(SolShowCardStackType.MIDDLE))
        {
            if (!srcCard.visibleRef.get())
            {
                return CanExecuteResponse.no("Can only pull visible cards");
            }

            Card card_parent = srcCard;
            List<Card> cards = srcCardStack.getCardsFrom(srcCard).getExcept(srcCard.id);
            for (int i = 0; i < cards.size(); i++)
            {
                Card card_child = cards.get(i);

                if (!card_child.visibleRef.get())
                {
                    return CanExecuteResponse.no("All pulled cards must be visible");
                }

                int order_parent = card_parent.value.getOrderAtoK();
                int order_child = card_child.value.getOrderAtoK();
                if (order_parent != order_child + 1)
                {
                    return CanExecuteResponse.no("order must differ exactly 1 between adjacent cards, e.g. a Q lies on a K");
                }

                SuitColor color_parent = card_parent.suit.getColor();
                SuitColor color_child = card_child.suit.getColor();
                if (color_parent == color_child)
                {
                    return CanExecuteResponse.no("Color of adjacent cards must alternate");
                }

                card_parent = card_child;
            }

            return CanExecuteResponse.yes();
        }
        else if (srcCardStack.cardStackType.equals(SolShowCardStackType.SPECIAL))
        {
            int idx = srcCardStack.cards.indexOf(srcCard);
            if (idx != srcCardStack.getCards().size() - 1)
            {
                return CanExecuteResponse.no("Only the top card can be pulled from a SPECIAL stack");
            }
            
            return CanExecuteResponse.yes();
        }
        else if (srcCardStack.cardStackType.equals(SolShowCardStackType.TURNOVER))
        {
            int idx = srcCardStack.cards.indexOf(srcCard);
            if (idx != srcCardStack.getCards().size() - 1)
            {
                return CanExecuteResponse.no("Only the top card can be pulled from a TURNOVER stack");
            }

            return CanExecuteResponse.yes();
        }

        throw new UnsupportedOperationException(
                "No such SolShowCardStackType supported: " + srcCardStack.cardStackType);
    }
}
