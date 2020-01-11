package gent.timdemey.cards.readonlymodel;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.cards.CardGame;

public final class ReadOnlyCardGame extends ReadOnlyEntityBase<CardGame>
{


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

    public ReadOnlyEntityList<ReadOnlyCardStack> getCardStacks()
    {
        return ReadOnlyEntityFactory.getOrCreateCardStackList(entity.getCardStacks());
    }

    public List<String> getCardStackTypes()
    {
        return entity.getCardStackTypes();
    }

    public ReadOnlyEntityList<ReadOnlyCard> getCards()
    {
        return ReadOnlyEntityFactory.getOrCreateCardList(entity.getCards());
    }
}
