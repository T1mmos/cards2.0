package gent.timdemey.cards.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

public final class E_CardStack {
    
    static class Converter extends ASerializer<E_CardStack>
    {
        @Override
        protected void write(SerializationContext<E_CardStack> sc) {
            writeUUID(sc, PROPERTY_CARDSTACK_ID, sc.src.cardStackId);
            writeString(sc, PROPERTY_CARDSTACK_TYPE, sc.src.cardStackType);  
            writeInt(sc, PROPERTY_CARDSTACK_NUMBER, sc.src.typeNumber);
            writeList(sc, PROPERTY_CARDS, sc.src.cards);
        }

        @Override
        protected E_CardStack read(DeserializationContext dc) {
            UUID _id = readUUID(dc, PROPERTY_CARDSTACK_ID);
            String _type = readString(dc, PROPERTY_CARDSTACK_TYPE);
            int _number = readInt(dc, PROPERTY_CARDSTACK_NUMBER);                   
            List<E_Card> _cards = readList(dc, PROPERTY_CARDS, E_Card.class);
                    
            return createCardStack(_cards, _type, _number, _id);
        }        
    }
    
    private final UUID cardStackId; 
    private final List<E_Card> cards; 
    private final String cardStackType;
    private final int typeNumber;

    private E_CardStack (List<E_Card> cards, String cardStackType, int typeNumber)
    {
        this(UUID.randomUUID(), cards, cardStackType, typeNumber);
    }
    
    private E_CardStack (UUID cardStackId, List<E_Card> cards, String cardStackType, int typeNumber)
    {
        Preconditions.checkNotNull(cardStackId);
        Preconditions.checkNotNull(cards);
        
        this.cardStackId = cardStackId;   
        this.cards = new ArrayList<>(cards);
        this.cardStackType = cardStackType;
        this.typeNumber = typeNumber;
    }
    
    static E_CardStack createCardStack (List<E_Card> cards, String cardStackType, int typeNumber, UUID cardStackId)
    {
        E_CardStack cs = new E_CardStack(cardStackId, cards, cardStackType, typeNumber);
        cards.forEach(card -> card.setCardStack(cs));
        return cs;
    }
    
    public static E_CardStack createCardStack (List<E_Card> cards, String cardStackType, int typeNumber)
    {
        return createCardStack(cards, cardStackType, typeNumber, UUID.randomUUID());
    }
    
    public String getCardStackType ()
    {
        return cardStackType;
    }
    
    public int getCardTypeNumber()
    {
        return typeNumber;
    }
    
    public E_Card getLowestCard ()
    {
        Preconditions.checkState(!cards.isEmpty());
        
        return cards.get(0);
    }
    
    public E_Card getHighestCard ()
    {
        Preconditions.checkState(!cards.isEmpty());
        
        return cards.get(cards.size() - 1);
    }
    
    public List<E_Card> getCardsFrom(E_Card card)
    {
        Preconditions.checkArgument(cards.contains(card));
        
        int idx = cards.indexOf(card);
        return new ArrayList<>(cards.subList(idx, cards.size()));
    }
    
    public List<E_Card> getHighestCards(int count)
    {
        Preconditions.checkArgument(0 < count && count <= cards.size());
        
        return new ArrayList<>(cards.subList(cards.size() - count, cards.size()));
    }
    
    public int getCardCountFrom(E_Card card)
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
        while (idx < cards.size() && !cards.get(idx).isVisible())
        {
            idx++;
        }
        return idx;
    }
    
    public boolean isEmpty()
    {
        return cards.isEmpty();
    }    
    
    public List<E_Card> getCards()
    {
        return Collections.unmodifiableList(cards);
    }
    
    public int indexOf(E_Card card)
    {
        return cards.indexOf(card);
    }
    
    public boolean contains (E_Card card)
    {
        return cards.contains(card);
    }
    
    public boolean contains (UUID cardId)
    {
        return cards.stream().anyMatch(card -> card.getCardId().equals(cardId));
    }
    
    
    public int getSize()
    {
        return cards.size();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof E_CardStack)){
            return false;
        }
        
        E_CardStack other = (E_CardStack) obj;
        return cardStackId.equals(other.cardStackId);              
    }
     
    @Override
    public int hashCode() {
        return cardStackId.hashCode();
    }
    
    @Override
    public String toString() {
        return "[" + cardStackType +"#"+typeNumber+", #cards="+cards.size()+", id=" +cardStackId + "]"; 
    }

    public UUID getCardStackId() {
        return cardStackId;
    }

    public void removeAll(List<E_Card> cards) {
        this.cards.removeAll(cards);
    }

    public void addAll(List<E_Card> cards) {
        List<E_Card> intersect = new ArrayList<>(this.cards);
        intersect.retainAll(cards);
        if (intersect.size() > 0)
        {
            throw new IllegalStateException("Attempted to add cards " + Arrays.toString(intersect.toArray()) + ", that are already in this stack: " + toString());
        }
        this.cards.addAll(cards);
    }

    public E_Card getCard(UUID cardId) {
        List<E_Card> filtered = cards.stream().filter(card -> card.getCardId().equals(cardId)).collect(Collectors.toList());
        
        assert filtered.size() == 1;
        
        return filtered.get(0);
    }
}
