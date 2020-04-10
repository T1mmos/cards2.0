package gent.timdemey.cards.model.entities.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.model.entities.cards.payload.P_CardGame;
import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.state.EntityStateListRef;
import gent.timdemey.cards.model.state.Property;

public class CardGame extends EntityBase
{
    public static final Property<CardStack> CardStacks = Property.of(CardGame.class, CardStack.class, "CardStacks");

    public final List<PlayerConfiguration> playerConfigurations;
    
    private final EntityStateListRef<CardStack> cardStacksRef;

    public CardGame(List<PlayerConfiguration> playerConfigurations)
    {
        List<CardStack> cardStacks = new ArrayList<>();
        for (PlayerConfiguration pc : playerConfigurations)
        {
            cardStacks.addAll(pc.cardStacks);
        }
        this.cardStacksRef = new EntityStateListRef<>(CardStacks, id, cardStacks);
        this.playerConfigurations = Collections.unmodifiableList(new ArrayList<>(playerConfigurations));
    }
    
    public CardGame(P_CardGame pl)
    {
        super(pl);
        
        List<CardStack> cardStacks = new ArrayList<>();
        for (PlayerConfiguration pc : pl.playerConfigurations)
        {
            cardStacks.addAll(pc.cardStacks);
        }
        this.cardStacksRef = new EntityStateListRef<>(CardStacks, id, cardStacks);
        this.playerConfigurations = Collections.unmodifiableList(new ArrayList<>(pl.playerConfigurations));
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
        Preconditions.checkNotNull(playerId);
        Preconditions.checkNotNull(cardStackType);

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
        Preconditions.checkNotNull(playerId);
        Preconditions.checkNotNull(cardStackType);

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
    
    @Override
    public String toDebugString()
    {
        return "";
    }
}
