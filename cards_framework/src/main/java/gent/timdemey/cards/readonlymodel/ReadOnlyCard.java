package gent.timdemey.cards.readonlymodel;

import gent.timdemey.cards.model.cards.Card;
import gent.timdemey.cards.model.cards.Suit;
import gent.timdemey.cards.model.cards.Value;

public final class ReadOnlyCard extends ReadOnlyEntityBase<Card> 
{
    ReadOnlyCard (Card card)
    {
        super(card);
    }
        
    public Suit getSuit()
    {
        return entity.suit;
    }
    
    public Value getValue()
    {
        return entity.value;
    }
        
    public boolean isVisible()
    {
        return entity.visibleRef.get();
    }
    
    public ReadOnlyCardStack getCardStack()
    {
        return ReadOnlyEntityFactory.getOrCreateCardStack(entity.cardStack);
    }
    
    public int getCardIndex()
    {
        return this.entity.getCardIndex();
    }
}
