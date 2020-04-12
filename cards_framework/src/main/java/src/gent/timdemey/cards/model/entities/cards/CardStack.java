package gent.timdemey.cards.model.entities.cards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.model.entities.cards.payload.P_CardStack;
import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.state.EntityStateListRef;
import gent.timdemey.cards.model.state.Property;
import gent.timdemey.cards.utils.Debug;

public class CardStack extends EntityBase
{
    public static final Property<Card> Cards = Property.of(CardStack.class, Card.class, "Cards");

    public final EntityStateListRef<Card> cards;
    public final String cardStackType;
    public final int typeNumber;

    public CardStack(String cardStackType, int typeNumber)
    {
        this.cardStackType = cardStackType;
        this.typeNumber = typeNumber;
        this.cards = new EntityStateListRef<>(Cards, id, new ArrayList<>());
    }

    public CardStack(P_CardStack pl)
    {
        super(pl);
        this.cardStackType = pl.cardStackType;
        this.typeNumber = pl.typeNumber;
        this.cards = new EntityStateListRef<>(Cards, id, pl.cards);
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
    
    public void removeAll(List<Card> cards)
    {
        this.cards.removeAll(cards);
    }

    @Override
    public String toDebugString()
    {        
        return Debug.getKeyValue("cardStackType", cardStackType) + 
               Debug.getKeyValue("typeNumber", typeNumber) + 
               Debug.getKeyValue("cardsCount", getCards().size()) +
               Debug.listEntity("cards", getCards());
    }
}
