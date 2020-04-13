package gent.timdemey.cards.model.entities.cards;

import gent.timdemey.cards.model.entities.cards.payload.P_Card;
import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.state.Property;
import gent.timdemey.cards.model.state.StateValueRef;
import gent.timdemey.cards.utils.Debug;

public class Card extends EntityBase
{
    public static final Property<Boolean> Visible = Property.of(Card.class, Boolean.class, "Visible");

    public final Suit suit;
    public final Value value;

    public final StateValueRef<Boolean> visibleRef;
    public CardStack cardStack;

    public Card(Suit suit, Value value, boolean visible)
    {
        this.suit = suit;
        this.value = value;
        this.visibleRef = new StateValueRef<>(Visible, id, visible);
        this.cardStack = null;
    }

    public Card(P_Card pl)
    {
        super(pl);
        this.suit = pl.suit;
        this.value = pl.value;
        this.visibleRef = new StateValueRef<>(Visible, id, pl.visible);
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
        if (cardNotation == null || cardNotation.length() != 2)
        {
            throw new IllegalArgumentException("Expected a valid short card notation but got: " + cardNotation);
        }
        
        String valueStr = cardNotation.substring(0, 1);
        String suitStr = cardNotation.substring(1, 2);
        Value value = Value.fromCharacter(valueStr);
        Suit suit = Suit.fromCharacter(suitStr);
        
        return this.value == value && this.suit == suit;
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
