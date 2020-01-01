package gent.timdemey.cards.model.cards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.model.EntityBase;

public class CardStack extends EntityBase
{
    public List<Card> cards; 
    public String cardStackType;
    public int typeNumber;
    
    public CardStack ()
    {
        super();
    }
    
    public CardStack(UUID id)
    {
        super(id);
    }
    
    public Card getLowestCard ()
    {
        Preconditions.checkState(!cards.isEmpty());
        
        Card card = cards.get(0);
        return card;
    }
    
    public Card getHighestCard ()
    {
        Preconditions.checkState(!cards.isEmpty());
        
        return cards.get(cards.size() - 1);
    }
    
    public List<Card> getCardsFrom(Card card)
    {
        Preconditions.checkArgument(cards.contains(card));
        
        int idx = cards.indexOf(card);
        return new ArrayList<>(cards.subList(idx, cards.size()));
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
        while (idx < cards.size() && !cards.get(idx).visible)
        {
            idx++;
        }
        return idx;
    }
    
    public boolean isEmpty()
    {
        return cards.isEmpty();
    }    
    
    public List<Card> getCards()
    {
        return Collections.unmodifiableList(cards);
    }
    
    public int indexOf(Card card)
    {
        return cards.indexOf(card);
    }
    
    public boolean contains (Card card)
    {
        return cards.contains(card);
    }
    
    public boolean contains (UUID cardId)
    {
        return cards.stream().anyMatch(card -> card.id.equals(cardId));
    }
        
    public int getSize()
    {
        return cards.size();
    }

    public void addAll(List<Card> cards) {
        List<Card> intersect = new ArrayList<>(this.cards);
        intersect.retainAll(cards);
        if (intersect.size() > 0)
        {
            throw new IllegalStateException("Attempted to add cards " + Arrays.toString(intersect.toArray()) + ", that are already in this stack: " + toString());
        }
        this.cards.addAll(cards);
    }
    
    @Override
    public String toString() {
        return "[" + cardStackType +"#"+typeNumber+", #cards="+cards.size()+", id=" +id + "]"; 
    }
}
