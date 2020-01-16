package gent.timdemey.cards.model.cards;

import java.util.UUID;

import gent.timdemey.cards.model.EntityBase;
import gent.timdemey.cards.model.state.Property;
import gent.timdemey.cards.model.state.StateValueRef;

public class Card extends EntityBase
{
    public static final Property Visible = Property.of(Card.class, "Visible");

    public final Suit suit;
    public final Value value;

    public final StateValueRef<Boolean> visibleRef;
    public CardStack cardStack;

    public Card(UUID id, Suit suit, Value value, boolean visible)
    {
        super(id);

        this.suit = suit;
        this.value = value;
        this.visibleRef = new StateValueRef<>(Visible, id);
        this.cardStack = null;
    }

    public Card(Suit suit, Value value, boolean visible)
    {
        this(UUID.randomUUID(), suit, value, visible);
    }

    public int getCardIndex()
    {
        return cardStack.getCards().indexOf(this);
    }

    @Override
    public String toString()
    {
        return "" + value + suit;
    }
}
