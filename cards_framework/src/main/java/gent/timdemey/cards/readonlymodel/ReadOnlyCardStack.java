package gent.timdemey.cards.readonlymodel;

import java.util.List;

import gent.timdemey.cards.model.entities.state.CardStack;

public final class ReadOnlyCardStack extends ReadOnlyEntityBase<CardStack>
{
    public static ReadOnlyProperty<ReadOnlyCard> Cards = ReadOnlyProperty.of(ReadOnlyCard.class, CardStack.Cards);
    
    ReadOnlyCardStack(CardStack cardStack)
    {
        super(cardStack);
    }

    public String getCardStackType()
    {
        return entity.cardStackType;
    }

    public int getTypeNumber()
    {
        return entity.typeNumber;
    }

    public ReadOnlyCard getLowestCard()
    {
        return ReadOnlyEntityFactory.getOrCreateCard(entity.getLowestCard());
    }

    public ReadOnlyCard getHighestCard()
    {
        return ReadOnlyEntityFactory.getOrCreateCard(entity.getHighestCard());
    }

    public List<ReadOnlyCard> getCardsFrom(ReadOnlyCard card)
    {
        return ReadOnlyEntityFactory.getOrCreateCardList(entity.getCardsFrom(card.entity));
    }

    public List<ReadOnlyCard> getHighestCards(int count)
    {
        return ReadOnlyEntityFactory.getOrCreateCardList(entity.getHighestCards(count));
    }

    public int getCardCountFrom(ReadOnlyCard card)
    {
        return entity.getCardCountFrom(card.entity);
    }

    public int getInvisibleCardCount()
    {
        return entity.getInvisibleCardCount();
    }

    public ReadOnlyList<ReadOnlyCard> getCards()
    {
        return ReadOnlyEntityFactory.getOrCreateCardList(entity.getCards());
    }
}
