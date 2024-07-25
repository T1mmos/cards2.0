package gent.timdemey.cards.model.entities.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.delta.EntityStateListRef;
import gent.timdemey.cards.model.delta.IChangeTracker;
import gent.timdemey.cards.model.delta.Property;
import gent.timdemey.cards.model.entities.common.EntityList;

public class CardGame extends EntityBase
{
    public static final Property<CardStack> CardStacks = Property.of(CardGame.class, CardStack.class, "CardStacks");

    public final List<PlayerConfiguration> playerConfigurations;
    
    private final EntityStateListRef<CardStack> cardStacksRef;

    CardGame(IChangeTracker changeTracker, UUID id, List<PlayerConfiguration> playerConfigurations)
    {
        super(id);
        
        List<CardStack> cardStacks = new ArrayList<>();
        for (PlayerConfiguration pc : playerConfigurations)
        {
            cardStacks.addAll(pc.cardStacks);
        }
        
        this.cardStacksRef = new EntityStateListRef<>(changeTracker, CardStacks, id, cardStacks);
        this.playerConfigurations = Collections.unmodifiableList(new ArrayList<>(playerConfigurations));
    }
    
    private Card getCardPriv(UUID cardId)
    {
        for (PlayerConfiguration pc : playerConfigurations)
        {
            for (CardStack cs : pc.cardStacks)
            {
                if (cs.getCards().contains(cardId))
                {
                    return cs.getCards().get(cardId);
                }
            }
        }
        return null;
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
        for (PlayerConfiguration pc : playerConfigurations)
        {
            for (CardStack cs : pc.cardStacks)
            {
                if (cs.id.equals(id))
                {
                    return true;
                }
            }
        }
        return false;
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
        if (playerId == null)
        {
            throw new IllegalArgumentException("playerId");
        }
        if (cardStackType == null)
        {
            throw new IllegalArgumentException("cardStackType");
        }

        List<CardStack> cardStacks = new ArrayList<>();

        List<CardStack> playerStacks = getCardStacksForPlayer(playerId);
        for (CardStack cs : playerStacks)
        {
            if (cs.cardStackType.equals(cardStackType))
            {
                cardStacks.add(cs);
            }
        }
       
        return cardStacks;
    }

    public List<CardStack> getCardStacks(String cardStackType)
    {
        List<CardStack> cardStacks = new ArrayList<>();
        for (PlayerConfiguration pc : playerConfigurations)
        {
            for (CardStack cs : pc.cardStacks)
            {
                if (cs.cardStackType.equals(cardStackType))
                {
                    cardStacks.add(cs);
                }
            }
        }
        return cardStacks;
    }
    
    public List<CardStack> getCardStacksForPlayer(UUID playerId)
    {
        for (PlayerConfiguration pc : playerConfigurations)
        {
            if (pc.playerId.equals(playerId))
            {
                return pc.cardStacks;
            }
        }
        return null;
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
        if (playerId == null)
        {
            throw new IllegalArgumentException("playerId");
        }
        if (cardStackType == null)
        {
            throw new IllegalArgumentException("cardStackType");
        }

        List<CardStack> cardStacks = getCardStacks(playerId, cardStackType);
        for (CardStack cs : cardStacks)
        {
            if (cs.typeNumber == typeNumber)
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
        return getPlayerId(card.cardStack);
    }

    public UUID getPlayerId(UUID cardStackId)
    {        
        for (PlayerConfiguration pc : playerConfigurations)
        {
            for (CardStack cs : pc.cardStacks)
            {
                if (cs.id.equals(cardStackId))
                {
                    return pc.playerId;
                }
            }
        }
        
        throw new IllegalArgumentException("No such card stack ID in the game: " + cardStackId);
    }

    public EntityList<Card> getCards()
    {
        EntityList<Card> cards = new EntityList<>();
        for (CardStack cs : cardStacksRef)
        {
            cards.addAll(cs.getCards());
        }
        
        return cards;
    }
    
    @Override
    public String toDebugString()
    {
        return "";
    }
}
