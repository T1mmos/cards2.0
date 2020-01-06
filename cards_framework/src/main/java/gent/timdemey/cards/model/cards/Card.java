package gent.timdemey.cards.model.cards;

import java.util.UUID;

import gent.timdemey.cards.model.EntityBase;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.model.state.StateValueRef;

public class Card extends EntityBase
{
    public final Suit suit;
    public final Value value;
    
    public StateValueRef<Boolean> visibleRef;
    public StateValueRef<CardStack> cardStackRef;    
    
    public Card (State state, UUID id, Suit suit, Value value, boolean visible)
    {
        super(id);
        
        this.suit = suit;
        this.value = value;        
        this.visibleRef = StateValueRef.create(state);
        this.cardStackRef = StateValueRef.create(state);
    }
    
    public Card (State state, Suit suit, Value value, boolean visible)
    {
        this(state, UUID.randomUUID(), suit, value, visible);
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
