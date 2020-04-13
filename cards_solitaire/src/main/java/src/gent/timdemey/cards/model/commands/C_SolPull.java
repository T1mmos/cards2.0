package gent.timdemey.cards.model.commands;

import java.util.List;
import java.util.UUID;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.cards.CardStack;
import gent.timdemey.cards.model.entities.commands.C_Pull;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.boot.SolitaireCardStackType;

public class C_SolPull extends C_Pull
{
    public C_SolPull(UUID srcCardStackId, UUID cardId)
    {
        super(srcCardStackId, cardId);
    }
    
    @Override
    protected boolean canPull(CardStack srcCardStack, Card srcCard, State state)
    {
        String srcType = srcCardStack.cardStackType;
        List<Card> cards = srcCardStack.getCards();        

        Preconditions.checkArgument(cards.contains(srcCard));
        
        // Can only pull visible cards
        if (!srcCard.visibleRef.get())
        {
            return false;
        }
        
        if (srcType.equals(SolitaireCardStackType.TURNOVER) || srcType.equals(SolitaireCardStackType.LAYDOWN))
        {
            return cards.get(cards.size() - 1) == srcCard; // 1 card
        }
        else if (srcType.equals(SolitaireCardStackType.MIDDLE))
        {
            int cardIndex = cards.indexOf(srcCard);
            for (int i = cardIndex; i < cards.size() - 1; i++)
            {
                Card lowerCard = cards.get(i);
                Card higherCard = cards.get(i+1);
               
                // cannot laydown on invisible cards
                if (!higherCard.visibleRef.get())
                {
                    return false;
                }
                
                // cannot laydown on same color
                if (lowerCard.suit.getColor() == higherCard.suit.getColor())
                {
                    return false;
                }
                
                // value of destination card must be 1 more
                if (lowerCard.value.getOrderAtoK() != higherCard.value.getOrderAtoK() + 1)
                {
                    return false;
                }
            }
            
            return true;            
        }
        
        return false;
    }
}
