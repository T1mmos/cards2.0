package gent.timdemey.cards.readonlymodel;

import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.cards.Suit;
import gent.timdemey.cards.model.entities.cards.Value;

public final class ReadOnlyCard extends ReadOnlyEntityBase<Card>
{
    public static final ReadOnlyProperty<Boolean> Visible = ReadOnlyProperty.of(Card.Visible);
    public static ReadOnlyProperty<Integer> Score = ReadOnlyProperty.of(Card.Score);
    
    ReadOnlyCard(Card card)
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
