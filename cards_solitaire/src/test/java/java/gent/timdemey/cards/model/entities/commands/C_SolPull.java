package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.commands.game.C_Pull;
import gent.timdemey.cards.di.Container;
import java.util.List;

import gent.timdemey.cards.model.entities.state.Card;
import gent.timdemey.cards.model.entities.state.CardStack;
import gent.timdemey.cards.model.entities.commands.game.P_Pull;
import gent.timdemey.cards.services.contract.descriptors.SolitaireComponentTypes;

public class C_SolPull extends C_Pull
{
    public C_SolPull(
        Container container,
        P_Pull parameters)
    {
        super(container, parameters);
    }
    
    @Override
    protected CanExecuteResponse canPull(CardStack srcCardStack, Card srcCard)
    {
        String srcType = srcCardStack.cardStackType;
        List<Card> cards = srcCardStack.getCards();        

        if (!cards.contains(srcCard))
        {
            throw new IllegalStateException("cards does not contain srcCard");
        }
        
        // Can only pull visible cards
        if (!srcCard.visibleRef.get())
        {
            return CanExecuteResponse.no("The source card isn't visible");
        }
        
        if (srcType.equals(SolitaireComponentTypes.TURNOVER) || srcType.equals(SolitaireComponentTypes.LAYDOWN))
        {
            if (cards.get(cards.size() - 1) != srcCard)
            {
                return CanExecuteResponse.no("Can only move 1 card at a time from TURNOVER to LAYDOWN");
            }
            return CanExecuteResponse.yes(); 
        }
        else if (srcType.equals(SolitaireComponentTypes.MIDDLE))
        {
            int cardIndex = cards.indexOf(srcCard);
            for (int i = cardIndex; i < cards.size() - 1; i++)
            {
                Card lowerCard = cards.get(i);
                Card higherCard = cards.get(i+1);
               
                if (!higherCard.visibleRef.get())
                {
                    return CanExecuteResponse.no("Cannot push onto invisible cards");
                }
                
                if (lowerCard.suit.getColor() == higherCard.suit.getColor())
                {
                    return CanExecuteResponse.no("Cannot push onto cards of the same color");
                }
                
                if (lowerCard.value.getOrderAtoK() != higherCard.value.getOrderAtoK() + 1)
                {
                    return CanExecuteResponse.no("Value of destination card must be 1 more");
                }
            }
            
            return CanExecuteResponse.yes();        
        }
        
        return CanExecuteResponse.no("Not a valid pull action");
    }
}
