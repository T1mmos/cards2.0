package gent.timdemey.cards.model.entities.cards;

import gent.timdemey.cards.model.entities.cards.payload.P_Card;
import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.state.Property;
import gent.timdemey.cards.model.state.StateValueRef;

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
        this.cardStack = pl.cardStack;
    }

    public int getCardIndex()
    {
        return cardStack.getCards().indexOf(this);
    }

    @Override
    public String toDebugString()
    {
        return String.format("Value=%s,Suit=%s", value, suit);
    }
}
