package gent.timdemey.cards.model.entities.state;

import gent.timdemey.cards.model.entities.state.payload.P_Card;
import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.delta.IChangeTracker;
import gent.timdemey.cards.model.delta.Property;
import gent.timdemey.cards.model.delta.StateValueRef;
import gent.timdemey.cards.utils.Debug;
import java.util.UUID;

public class Card extends EntityBase
{
    public static final Property<Boolean> Visible = Property.of(Card.class, Boolean.class, "Visible");
    public static final Property<Integer> Score = Property.of(Card.class, Integer.class, "Score");

    public final CardSuit suit;
    public final CardValue value;

    public final StateValueRef<Boolean> visibleRef;
    public final StateValueRef<Integer> scoreRef;
    public CardStack cardStack;    
        
    Card(IChangeTracker changeTracker, UUID id, CardSuit suit, CardValue value, boolean visible)
    {
        super(id);
        
        this.suit = suit;
        this.value = value;
        this.visibleRef = new StateValueRef<>(changeTracker, Visible, id, visible);
        this.scoreRef = new StateValueRef<>(changeTracker, Score, id, 0);
        this.cardStack = null;
    }
    
    /**
     * Checks whether this card is equal to the given short card notation.
     * For example, a valid short notation is "8♠". Infact, a combination
     * of the textual form of a Value and a Suit is required (in this order).
     * Passing invalid short notations will result in an exception.
     * @param shortNotation
     * @return
     */
    public final boolean equalsNotation(String cardNotation)
    {
        if (cardNotation == null || (cardNotation.length() != 2 && cardNotation.length() != 3)) // e.g. 10x has length 3
        {
            throw new IllegalArgumentException("Expected a valid short card notation but got: " + cardNotation);
        }
        
        String valueStr = cardNotation.substring(0, cardNotation.length() - 1);
        String suitStr = cardNotation.substring(cardNotation.length() - 1, cardNotation.length());

        return getNotation().equals(valueStr + suitStr);
    }
    
    public final String getNotation()
    {
        String valueStr = value.getTextual();
        String suitStr = suit.getTextual();
        return valueStr + suitStr;
    }
    
    public final boolean equals(String cardNotation)
    {
        return equalsNotation(cardNotation);
    }

    public int getCardIndex()
    {
        return cardStack.getCards().indexOf(this);
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("shortNotation", value.getTextual() + suit.getTextual());
    }
}
