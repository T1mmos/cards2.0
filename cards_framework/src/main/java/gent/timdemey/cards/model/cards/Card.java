package gent.timdemey.cards.model.cards;

import java.util.UUID;

import gent.timdemey.cards.model.EntityBase;

public class Card extends EntityBase
{
    public final Suit suit;
    public final Value value;
    
    public boolean visible;
    public CardStack cardStack;    
    
    public Card (UUID id, Suit suit, Value value, boolean visible)
    {
        super(id);
        
        this.suit = suit;
        this.value = value;        
        this.visible = visible;
    }
    
    public Card (Suit suit, Value value, boolean visible)
    {
        this(UUID.randomUUID(), suit, value, visible);
    }
    
    public int getCardIndex()
    {
        return cardStack.indexOf(this);
    }    
    
    @Override
    public String toString()
    {
        return "" + value + suit;
    }
}
