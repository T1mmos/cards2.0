package gent.timdemey.cards.model.cards;

import java.util.UUID;

import gent.timdemey.cards.model.EntityBase;
import gent.timdemey.cards.model.state.StateValueRef;

public class Card extends EntityBase
{
    public final Suit suit;
    public final Value value;
    
    public final StateValueRef<Boolean> visibleRef;
    public final StateValueRef<CardStack> cardStackRef;   
    
    public Card (UUID id, Suit suit, Value value, boolean visible)
    {
        super(id);
        
        this.suit = suit;
        this.value = value;        
        this.visibleRef = new StateValueRef<>();
        this.cardStackRef = new StateValueRef<>();
    }
    
    public Card (Suit suit, Value value, boolean visible)
    {
        this(UUID.randomUUID(), suit, value, visible);
    }
    
    public int getCardIndex()
    {
        return cardStackRef.get().getCards().indexOf(this);
    }    
    
    @Override
    public String toString()
    {
        return "" + value + suit;
    }
}
