package gent.timdemey.cards.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

public final class E_CardGame {

    
    static class Converter extends  ASerializer<E_CardGame> {

        @Override
        protected void write(SerializationContext<E_CardGame> sc) {
            // TODO Auto-generated method stub
            
        }

        @Override
        protected E_CardGame read(DeserializationContext dc) {
            // TODO Auto-generated method stub
            return null;
        }
    }

    private final Map<UUID, E_CardStack> cardStacks;
    private final Map<UUID, List<UUID>> playerStacks;

    E_CardGame(Map<UUID, List<E_CardStack>> playerStacks) {
        this.cardStacks = new HashMap<>();
        this.playerStacks = new HashMap<>();
        
        for(UUID playerId : playerStacks.keySet())
        {
            List<UUID> cardStackIds = new ArrayList<>();
            List<E_CardStack> cardStacks = playerStacks.get(playerId);
            for (int i = 0; i < cardStacks.size(); i++)
            {
                E_CardStack cardStack = cardStacks.get(i);
                UUID cardStackId = cardStack.getCardStackId();
                
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
    public List<E_Card> getUniqueCards ()
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
    public List<E_Card> getUniqueCards(UUID playerId)
    {
        return playerStacks.get(playerId).stream()
            .map(id -> cardStacks.get(id))
            .flatMap(cs -> cs.getCards().stream())
            .distinct()
            .collect(Collectors.toList());
    }
    
    private E_Card getCardPriv (UUID cardId)
    {
        List<E_Card> cards = cardStacks.values().stream()
                .flatMap(cs -> cs.getCards().stream())     
                .filter(c -> c.getCardId().equals(cardId))
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

    public E_Card getCard(UUID cardId) 
    {
        E_Card card = getCardPriv(cardId);
        if (card == null)
        {
            throw new IllegalArgumentException("No such card ID in the current game: " + cardId);
        }
        return card;
    }
    
    public boolean isCard(UUID cardId)
    {
        E_Card card = getCardPriv(cardId);
        return card != null;
    }
    
    
    public List<E_CardStack> getCardStacks()
    {
        return new ArrayList<>(cardStacks.values());                
    }
    
    public List<String> getCardStackTypes()
    {
        return cardStacks.values().stream()
                .map(cs -> cs.getCardStackType())
                .collect(Collectors.toList());
    }
    
    public List<E_CardStack> getCardStacks(UUID playerId, String cardStackType) {
        Preconditions.checkNotNull(playerId);
        Preconditions.checkNotNull(cardStackType);

        return playerStacks.get(playerId).stream()
                .map(id -> cardStacks.get(id))
                .filter(cs -> cs.getCardStackType().equals(cardStackType))
                .collect(Collectors.toList());
    }

    public E_CardStack getCardStack(UUID playerId, String cardStackType, int typeNumber) {
        List<E_CardStack> stacks = playerStacks.get(playerId).stream()
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
    
    public UUID getPlayerId(E_CardStack cardStack)
    {
        return getPlayerId(cardStack.getCardStackId());
    }
    
    public UUID getPlayerId(E_Card card)
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
    
    private E_CardStack getCardStackPriv(UUID cardStackId)
    {
        if (cardStacks.containsKey(cardStackId))
        {
            return cardStacks.get(cardStackId);
        }
        return null;
    }
    
    public E_CardStack getCardStack(UUID cardStackId)
    {
        E_CardStack cardStack = getCardStackPriv(cardStackId);
        
        if (cardStack == null)
        {
            throw new IllegalArgumentException("No such card stack ID in the current game: " + cardStackId);
        }
        return cardStack;
    }
    
    public boolean isCardStack(UUID cardStackId)
    {
        E_CardStack cardStack = getCardStackPriv(cardStackId);
        return cardStack != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof E_CardGame)) {
            return false;
        }

        E_CardGame other = (E_CardGame) obj;
        return Objects.equals(playerStacks, other.playerStacks) 
                && Objects.equals(cardStacks, other.cardStacks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerStacks, cardStacks);
    }

    @Override
    public String toString() {
        return Json.getPretty().toJson(this);
    }
}
