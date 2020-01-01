package gent.timdemey.cards.model.cards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.model.EntityBase;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;

public class CardGame extends EntityBase
{

    private final Map<UUID, ReadOnlyCardStack> cardStacks;
    private final Map<UUID, List<UUID>> playerStacks;
    
    public CardGame(Map<UUID, List<ReadOnlyCardStack>> playerStacks)
    {
        this.cardStacks = new HashMap<>();
        this.playerStacks = new HashMap<>();
        
        for(UUID playerId : playerStacks.keySet())
        {
            List<UUID> cardStackIds = new ArrayList<>();
            List<ReadOnlyCardStack> cardStacks = playerStacks.get(playerId);
            for (int i = 0; i < cardStacks.size(); i++)
            {
                ReadOnlyCardStack cardStack = cardStacks.get(i);
                UUID cardStackId = cardStack.getId();
                
                cardStackIds.add(cardStackId);
                this.cardStacks.put(cardStackId, cardStack);
            }
            this.playerStacks.put(playerId, cardStackIds);
        }
    }
    
    /**
     * Gets all distinct cards in the game. If two or more players have the same card, that card
     * is only present once in the returned list.
     * @return
     */
    public List<ReadOnlyCard> getUniqueCards ()
    {
        return cardStacks.values().stream()
            .flatMap(cs -> cs.getCards().stream())            
            .distinct()
            .collect(Collectors.toList());
    }
    
    /**
     * Gets all distinct cards for a specific player.
     * @return
     */
    public List<ReadOnlyCard> getUniqueCards(UUID playerId)
    {
        return playerStacks.get(playerId).stream()
            .map(id -> cardStacks.get(id))
            .flatMap(cs -> cs.getCards().stream())
            .distinct()
            .collect(Collectors.toList());
    }
    
    private ReadOnlyCard getCardPriv (UUID cardId)
    {
        List<ReadOnlyCard> cards = cardStacks.values().stream()
                .flatMap(cs -> cs.getCards().stream())     
                .filter(c -> c.getId().equals(cardId))
                .collect(Collectors.toList());
        if (cards.size() > 1)
        {
            throw new IllegalStateException("Multiple cards found with given id: " + cardId);
        }
        if (cards.size() == 1)
        {
            return cards.get(0);
        }
        return null;
    }

    public ReadOnlyCard getCard(UUID cardId) 
    {
        ReadOnlyCard card = getCardPriv(cardId);
        if (card == null)
        {
            throw new IllegalArgumentException("No such card ID in the current game: " + cardId);
        }
        return card;
    }
    
    public boolean isCard(UUID cardId)
    {
        ReadOnlyCard card = getCardPriv(cardId);
        return card != null;
    }
    
    
    public List<ReadOnlyCardStack> getCardStacks()
    {
        return new ArrayList<>(cardStacks.values());                
    }
    
    public List<String> getCardStackTypes()
    {
        return cardStacks.values().stream()
                .map(cs -> cs.getCardStackType())
                .collect(Collectors.toList());
    }
    
    public List<ReadOnlyCardStack> getCardStacks(UUID playerId, String cardStackType) {
        Preconditions.checkNotNull(playerId);
        Preconditions.checkNotNull(cardStackType);

        return playerStacks.get(playerId).stream()
                .map(id -> cardStacks.get(id))
                .filter(cs -> cs.getCardStackType().equals(cardStackType))
                .collect(Collectors.toList());
    }

    public ReadOnlyCardStack getCardStack(UUID playerId, String cardStackType, int typeNumber) {
        List<ReadOnlyCardStack> stacks = playerStacks.get(playerId).stream()
                .map(id -> cardStacks.get(id))
                .filter(cs -> cs.getCardStackType().equals(cardStackType))
                .filter(cs -> cs.getCardTypeNumber() == typeNumber)
                .collect(Collectors.toList());
        if (stacks.size() != 1)
        {
            throw new IllegalArgumentException();
        }
        return stacks.get(0);
    }
    
    public UUID getPlayerId(ReadOnlyCardStack cardStack)
    {
        return getPlayerId(cardStack.getId());
    }
    
    public UUID getPlayerId(ReadOnlyCard card)
    {
        return getPlayerId(card.getCardStack());
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
    
    private ReadOnlyCardStack getCardStackPriv(UUID cardStackId)
    {
        if (cardStacks.containsKey(cardStackId))
        {
            return cardStacks.get(cardStackId);
        }
        return null;
    }
    
    public ReadOnlyCardStack getCardStack(UUID cardStackId)
    {
        ReadOnlyCardStack cardStack = getCardStackPriv(cardStackId);
        
        if (cardStack == null)
        {
            throw new IllegalArgumentException("No such card stack ID in the current game: " + cardStackId);
        }
        return cardStack;
    }
    
    public boolean isCardStack(UUID cardStackId)
    {
        ReadOnlyCardStack cardStack = getCardStackPriv(cardStackId);
        return cardStack != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ReadOnlyCardGame)) {
            return false;
        }
        
        CardGame other = (CardGame) obj;

        return Objects.equals(playerStacks, other.playerStacks) 
                && Objects.equals(cardStacks, other.cardStacks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerStacks, cardStacks);
    }
}
