package gent.timdemey.cards.model.cards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.model.EntityBase;
import gent.timdemey.cards.model.state.EntityStateListRef;

public class CardGame extends EntityBase
{
    private final EntityStateListRef<CardStack> cardStacksRef;
    private final Map<UUID, List<UUID>> playerStacks;

    public CardGame(Map<UUID, List<CardStack>> playerStacks)
    {
        this.cardStacksRef = new EntityStateListRef<>(new ArrayList<>());
        this.playerStacks = new HashMap<>();

        for (UUID playerId : playerStacks.keySet())
        {
            List<UUID> cardStackIds = new ArrayList<>();
            List<CardStack> cardStacks = playerStacks.get(playerId);
            for (int i = 0; i < cardStacks.size(); i++)
            {
                CardStack cardStack = cardStacks.get(i);
                UUID cardStackId = cardStack.id;

                cardStackIds.add(cardStackId);
            }
            this.playerStacks.put(playerId, cardStackIds);
        }
    }

    private Card getCardPriv(UUID cardId)
    {
        for (CardStack cs : cardStacksRef)
        {
            if (cs.getCards().contains(cardId))
            {
                return cs.getCards().get(cardId);
            }
        }
        throw new IllegalArgumentException("No CardStack in the game contains a card with CardId=" + cardId);
    }

    public Card getCard(UUID cardId)
    {
        Card card = getCardPriv(cardId);
        if (card == null)
        {
            throw new IllegalArgumentException("No such card ID in the current game: " + cardId);
        }
        return card;
    }

    public boolean isCard(UUID cardId)
    {
        Card card = getCardPriv(cardId);
        return card != null;
    }

    public boolean isCardStack(UUID id)
    {
        return cardStacksRef.stream().anyMatch(cs -> cs.id.equals(id));
    }

    public EntityStateListRef<CardStack> getCardStacks()
    {
        return cardStacksRef;
    }

    public List<String> getCardStackTypes()
    {
        List<String> csTypes = new ArrayList<>();
        for (CardStack cs : cardStacksRef)
        {
            if (!csTypes.contains(cs.cardStackType))
            {
                csTypes.add(cs.cardStackType);
            }
        }
        return csTypes;
    }

    public List<CardStack> getCardStacks(UUID playerId, String cardStackType)
    {
        Preconditions.checkNotNull(playerId);
        Preconditions.checkNotNull(cardStackType);

        List<CardStack> cardStacks = new ArrayList<>();

        List<UUID> stackIds = playerStacks.get(playerId);
        for (UUID csId : stackIds)
        {
            CardStack cs = cardStacksRef.get(csId);
            if (cs.cardStackType.equals(cardStackType))
            {
                cardStacks.add(cs);
            }
        }

        return cardStacks;
    }

    public CardStack getCardStack(UUID id)
    {
        for (CardStack cs : cardStacksRef)
        {
            if (cs.id.equals(id))
            {
                return cs;
            }
        }

        throw new IllegalArgumentException("No CardStack in this CardGame with id=" + id);
    }

    public CardStack getCardStack(UUID playerId, String cardStackType, int typeNumber)
    {
        Preconditions.checkNotNull(playerId);
        Preconditions.checkNotNull(cardStackType);

        List<UUID> stackIds = playerStacks.get(playerId);
        for (UUID csId : stackIds)
        {
            CardStack cs = cardStacksRef.get(csId);
            if (cs.cardStackType.equals(cardStackType) && cs.typeNumber == typeNumber)
            {
                return cs;
            }
        }

        throw new IllegalArgumentException(
                "Player " + playerId + " has no CardStack of type " + cardStackType + " numbered " + typeNumber);
    }

    public UUID getPlayerId(CardStack cardStack)
    {
        return getPlayerId(cardStack.id);
    }

    public UUID getPlayerId(Card card)
    {
        return getPlayerId(card.cardStackRef.get());
    }

    public UUID getPlayerId(UUID cardStackId)
    {
        for (UUID playerId : playerStacks.keySet())
        {
            if (playerStacks.get(playerId).contains(cardStackId))
            {
                return playerId;
            }
        }
        throw new IllegalArgumentException("No such card stack ID in the game: " + cardStackId);
    }

    public EntityStateListRef<Card> getCards()
    {
        List<Card> cards = new ArrayList<>();
        for (CardStack cs : cardStacksRef)
        {
            cards.addAll(cs.getCards());
        }
        EntityStateListRef<Card> list = EntityStateListRef.asReadOnly(cards);
        return list;
    }
}
