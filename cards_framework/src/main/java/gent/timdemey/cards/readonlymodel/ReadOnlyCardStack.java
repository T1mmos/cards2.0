package gent.timdemey.cards.readonlymodel;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.cards.CardStack;

public final class ReadOnlyCardStack extends ReadOnlyEntityBase<CardStack>
{  

    ReadOnlyCardStack (CardStack cardStack)
    {
        super(cardStack);
    }
         
    public String getCardStackType ()
    {
        return entity.cardStackType;
    }
    
    public int getCardTypeNumber()
    {
        return entity.typeNumber;
    }
    
    public ReadOnlyCard getLowestCard ()
    {
        return ReadOnlyEntityFactory.getOrCreateCard(entity.getLowestCard ());
    }
    
    public ReadOnlyCard getHighestCard ()
    {        
        return ReadOnlyEntityFactory.getOrCreateCard(entity.getHighestCard());
    }
    
    public List<ReadOnlyCard> getCardsFrom(ReadOnlyCard card)
    {
        return ReadOnlyEntityFactory.getOrCreateCards(entity.getCardsFrom(card.entity));
    }
    
    public List<ReadOnlyCard> getHighestCards(int count)
    {
        return ReadOnlyEntityFactory.getOrCreateCards(entity.getHighestCards(count));
    }
    
    public int getCardCountFrom(ReadOnlyCard card)
    {
        return entity.getCardCountFrom(card.entity);
    }
    
    public int getInvisibleCardCount()
    {
        return entity.getInvisibleCardCount();
    }
    
    public boolean isEmpty()
    {
        return entity.isEmpty();
    }    
    
    public List<ReadOnlyCard> getCards()
    {
        return ReadOnlyEntityFactory.getOrCreateCards(entity.getCards());
    }
    
    public int indexOf(ReadOnlyCard card)
    {
        return entity.indexOf(card.entity);
    }
    
    public boolean contains (ReadOnlyCard card)
    {
        return entity.contains(card.entity);
    }
    
    public boolean contains (UUID cardId)
    {
        return entity.contains(cardId);
    }    
    
    public int getSize()
    {
        return entity.getSize();
    }
}
