package gent.timdemey.cards.model.commands;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.cards.CardStack;
import gent.timdemey.cards.model.entities.cards.SuitColor;
import gent.timdemey.cards.model.entities.commands.C_Pull;
import gent.timdemey.cards.services.boot.SolShowCardStackType;

public class C_SolShowPull extends C_Pull
{
    public C_SolShowPull(UUID srcCardStackId, UUID cardId)
    {
        super(srcCardStackId, cardId);
    }

    @Override
    protected boolean canPull(CardStack srcCardStack, Card srcCard)
    {
        if (srcCardStack.cardStackType.equals(SolShowCardStackType.DEPOT) || 
            srcCardStack.cardStackType.equals(SolShowCardStackType.LAYDOWN))
        {
            return false;
        }
        
        if (srcCardStack.cardStackType.equals(SolShowCardStackType.MIDDLE))
        {
            if (!srcCard.visibleRef.get())
            {
                return false;
            }
            
            Card card_parent = srcCard;
            List<Card> cards = srcCardStack.getCardsFrom(srcCard).getExcept(srcCard.id);
            for (int i = 0; i < cards.size(); i++)
            {
                Card card_child = cards.get(i);
                
                // entire stack must be visible
                if (!card_child.visibleRef.get())
                {
                    return false;
                }
                
                // order must differ exactly 1, e.g. a Q lies on a K
                int order_parent = card_parent.value.getOrderAtoK();
                int order_child = card_child.value.getOrderAtoK();
                if (order_parent != order_child + 1)
                {
                    return false;
                }
                
                // color must differ
                SuitColor color_parent = card_parent.suit.getColor();
                SuitColor color_child = card_child.suit.getColor();
                if (color_parent == color_child)
                {
                    return false;
                }
                
                card_parent = card_child;
            }
            
            return true;
        }
        else if (srcCardStack.cardStackType.equals(SolShowCardStackType.SPECIAL))
        {
            return true;
        }
        else if (srcCardStack.cardStackType.equals(SolShowCardStackType.TURNOVER))
        {
            // only the top card can be pulled
            int idx = srcCardStack.cards.indexOf(srcCard);
            if (idx != srcCardStack.getCards().size() - 1)
            {
                return false;
            }
            
            return true;
        }
        
        throw new UnsupportedOperationException("No such SolShowCardStackType supported: " + srcCardStack.cardStackType);
    }
}
