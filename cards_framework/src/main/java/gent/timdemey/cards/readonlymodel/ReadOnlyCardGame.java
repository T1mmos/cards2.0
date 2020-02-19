package gent.timdemey.cards.readonlymodel;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.cards.CardGame;

public final class ReadOnlyCardGame extends ReadOnlyEntityBase<CardGame>
{
    public static ReadOnlyProperty<ReadOnlyCardStack> CardStacks = ReadOnlyProperty.of(ReadOnlyCardStack.class, CardGame.CardStacks);

    ReadOnlyCardGame(CardGame cardGame)
    {
        super(cardGame);
    }

    public ReadOnlyCard getCard(UUID cardId)
    {
        return ReadOnlyEntityFactory.getOrCreateCard(entity.getCard(cardId));
    }

    public boolean isCard(UUID cardId)
    {
        return entity.isCard(cardId);
    }

    public boolean isCardStack(UUID id)
    {
        return entity.isCardStack(id);
    }

    public ReadOnlyCardStack getCardStack(UUID id)
    {
        return ReadOnlyEntityFactory.getOrCreateCardStack(entity.getCardStack(id));
    }

    public ReadOnlyCardStack getCardStack(UUID playerId, String cardStackType, int typeNumber)
    {
        return ReadOnlyEntityFactory.getOrCreateCardStack(entity.getCardStack(playerId, cardStackType, typeNumber));
    }

    public ReadOnlyEntityList<ReadOnlyCardStack> getCardStacks()
    {
        return ReadOnlyEntityFactory.getOrCreateCardStackList(entity.getCardStacks());
    }

    public List<ReadOnlyCardStack> getCardStacks(UUID playerId, String cardStackType)
    {
        return ReadOnlyEntityFactory.getOrCreateCardStackList(entity.getCardStacks(playerId, cardStackType));
    }

    public List<String> getCardStackTypes()
    {
        return entity.getCardStackTypes();
    }

    public ReadOnlyEntityList<ReadOnlyCard> getCards()
    {
        return ReadOnlyEntityFactory.getOrCreateCardList(entity.getCards());
    }
    
    public UUID getPlayerId(ReadOnlyCard card)
    {
        return entity.getPlayerId(card.entity);
    }
    
    public UUID getPlayerId(ReadOnlyCardStack cardStack)
    {
        return entity.getPlayerId(cardStack.entity);
    }
    
    public UUID getPlayerId(UUID cardStackId)
    {
        return entity.getPlayerId(cardStackId);
    }
}
