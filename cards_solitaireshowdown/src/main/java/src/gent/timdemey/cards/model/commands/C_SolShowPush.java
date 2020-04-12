package gent.timdemey.cards.model.commands;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.cards.CardStack;
import gent.timdemey.cards.model.entities.cards.Value;
import gent.timdemey.cards.model.entities.commands.C_Push;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.boot.SolShowCardStackType;

public class C_SolShowPush extends C_Push
{
    public C_SolShowPush(UUID dstCardStackId, List<UUID> srcCardIds)
    {
        super(dstCardStackId, srcCardIds);
    }

    @Override
    protected boolean canPush(CardStack dstCardStack, List<Card> srcCards, State state)
    {
        if (dstCardStack.cardStackType.equals(SolShowCardStackType.DEPOT) || 
            dstCardStack.cardStackType.equals(SolShowCardStackType.TURNOVER) || 
            dstCardStack.cardStackType.equals(SolShowCardStackType.SPECIAL))
        {
            return false;
        }
        
        if (dstCardStack.cardStackType.equals(SolShowCardStackType.MIDDLE))
        {
            UUID playerId = getSourceId();
            if (!playerId.equals(state.getServerId()) && !playerId.equals(state.getCardGame().getPlayerId(dstCardStack)))
            {
                return false;
            }
            
            
            // in solitaire showdown you are allowed to put any card on an empty stack,
            // not just a king
            if (dstCardStack.cards.isEmpty())
            {
                return true;
            }
            
            Card dstCard = dstCardStack.getHighestCard();
            Card srcCard = srcCards.get(0);
            
            // suit color must differ
            if (dstCard.suit.getColor() == srcCard.suit.getColor()) 
            {
                return false;
            }
            
            // value of destination card must be 1 more than source card 
            int value_src = srcCard.value.getOrderAtoK();
            int value_dst = dstCard.value.getOrderAtoK();
            if (value_dst != value_src + 1)
            {
                return false;
            }
            
            return true;
        }       
        else if (dstCardStack.cardStackType.equals(SolShowCardStackType.LAYDOWN))
        {
            // can only push 1 card at a time
            if (srcCards.size() != 1)
            {
                return false;
            }
            Card srcCard = srcCards.get(0);
            
            // the card must be visible
            if (!srcCard.visibleRef.get())
            {
                return false;
            }
            
            if (srcCard.value == Value.V_A)
            {
                // an ace requires an empty stack
                if (!dstCardStack.cards.isEmpty())
                {
                    return false;
                }
                
                return true;
            }
            else 
            {
                // not an ace -> stack must not be empty
                if (dstCardStack.cards.isEmpty())
                {
                    return false;
                }
                Card dstCard = dstCardStack.getHighestCard();
                
                // suit must match
                if (dstCard.suit != srcCard.suit)
                {
                    return false;
                }
                
                // value of source card must be 1 more than destination card
                int value_src = srcCard.value.getOrderAtoK();
                int value_dst = dstCard.value.getOrderAtoK();
                if (value_dst + 1 != value_src)
                {
                    return false;
                }
                
                return true;
            }
        }
        
        throw new UnsupportedOperationException("No such SolShowCardStackType supported: " + dstCardStack.cardStackType);
    }
}
