package gent.timdemey.cards.model.cards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.model.EntityBase;
import gent.timdemey.cards.model.state.EntityStateListRef;
import gent.timdemey.cards.model.state.Property;

public class CardStack extends EntityBase
{
    public static final Property Cards = Property.of(CardStack.class, "Cards");

    public final EntityStateListRef<Card> cards;
    public final String cardStackType;
    public final int typeNumber;

    public CardStack(String cardStackType, int typeNumber)
    {
        this(UUID.randomUUID(), cardStackType, typeNumber);
    }

    public CardStack(UUID id, String cardStackType, int typeNumber)
    {
        super(id);

        this.cardStackType = cardStackType;
        this.typeNumber = typeNumber;
        this.cards = new EntityStateListRef<>(Cards, id, new ArrayList<>());
    }

    public Card getLowestCard()
    {
        Preconditions.checkState(!cards.isEmpty());

        Card card = cards.get(0);
        return card;
    }

    public Card getHighestCard()
    {
        Preconditions.checkState(!cards.isEmpty());

        return cards.get(cards.size() - 1);
    }

    public EntityStateListRef<Card> getCardsFrom(Card card)
    {
        Preconditions.checkArgument(cards.contains(card));

        int idx = cards.indexOf(card);
        return EntityStateListRef.asReadOnly(cards.subList(idx, cards.size()));
    }

    public List<Card> getHighestCards(int count)
    {
        Preconditions.checkArgument(0 < count && count <= cards.size());

        return new ArrayList<>(cards.subList(cards.size() - count, cards.size()));
    }

    public int getCardCountFrom(Card card)
    {
        Preconditions.checkArgument(cards.contains(card));

        int idx = cards.indexOf(card);
        return cards.size() - idx;
    }

    public int getInvisibleCardCount()
    {
        if (cards.size() == 0)
        {
            return 0;
        }
        int idx = 0;
        while (idx < cards.size() && !cards.get(idx).visibleRef.get())
        {
            idx++;
        }
        return idx;
    }

    public EntityStateListRef<Card> getCards()
    {
        return cards;
    }

    public void addAll(List<Card> cards)
    {
        List<Card> intersect = new ArrayList<>(this.cards);
        intersect.retainAll(cards);
        if (intersect.size() > 0)
        {
            throw new IllegalStateException("Attempted to add cards " + Arrays.toString(intersect.toArray())
                    + ", that are already in this stack: " + toString());
        }
        this.cards.addAll(cards);
    }

    @Override
    public String toString()
    {
        return "[" + cardStackType + "#" + typeNumber + ", #cards=" + cards.size() + ", id=" + id + "]";
    }
}
