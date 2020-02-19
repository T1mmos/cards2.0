package gent.timdemey.cards.model.commands;

import java.util.List;
import java.util.UUID;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.entities.SolitaireCardStackType;
import gent.timdemey.cards.model.cards.Card;
import gent.timdemey.cards.model.cards.CardStack;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_SolPull extends CommandBase
{
    private final UUID srcCardStackId;
    private final UUID cardId;

    public C_SolPull(UUID srcCardStackId, UUID cardId)
    {
        this.srcCardStackId = srcCardStackId;
        this.cardId = cardId;
    }
    
    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        CardStack srcCardStack = state.getCardGame().getCardStack(srcCardStackId);
        Card card = state.getCardGame().getCard(cardId);
        
        String srcType = srcCardStack.cardStackType;
        List<Card> cards = srcCardStack.getCards();        

        Preconditions.checkArgument(cards.contains(card));
        
        // Can only pull visible cards
        if (!card.visibleRef.get())
        {
            return false;
        }
        
        if (srcType.equals(SolitaireCardStackType.TURNOVER) || srcType.equals(SolitaireCardStackType.LAYDOWN))
        {
            return cards.get(cards.size() - 1) == card; // 1 card
        }
        else if (srcType.equals(SolitaireCardStackType.MIDDLE))
        {
            int cardIndex = cards.indexOf(card);
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

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        // TODO Auto-generated method stub
        
    }

}
