package gent.timdemey.cards.model.entities.cards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        if (cards.isEmpty())
        {
            throw new IllegalStateException("cards cannot be empty");
        }

        Card card = cards.get(0);
        return card;
    }

    public Card getCardAt(int idx)
    {
        Card card = cards.get(idx);
        return card;
    }
    
    public Card getHighestCard()
    {
        if (cards.isEmpty())
        {
            throw new IllegalStateException("cards cannot be empty");
        }

        return cards.get(cards.size() - 1);
    }

    public EntityStateListRef<Card> getCardsFrom(Card card)
    {
        if (!cards.contains(card))
        {
            throw new IllegalStateException("this cardstack doesn't contain the given card");
        }

        int idx = cards.indexOf(card);
        return EntityStateListRef.asReadOnly(cards.subList(idx, cards.size()));
    }

    public List<Card> getHighestCards(int count)
    {
        if (!(0 < count && count <= cards.size()))
        {
            throw new IllegalStateException("cannot return " + count + " cards as this cardstack only contains " + cards.size() + " cards");
        }

        return new ArrayList<>(cards.subList(cards.size() - count, cards.size()));
    }

    public int getCardCountFrom(Card card)
    {
        if (!cards.contains(card))
        {
            throw new IllegalStateException("this cardstack doesn't contain the given card");
        }

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
