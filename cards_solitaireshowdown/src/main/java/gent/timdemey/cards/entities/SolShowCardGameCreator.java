package gent.timdemey.cards.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SolShowCardGameCreator implements ICardGameCreator {   
    

    @Override
    public Map<UUID, List<E_CardStack>> createStacks(List<UUID> playerIds, List<List<E_Card>> playerCards) 
    {
        Map<UUID, List<E_CardStack>> playersCardConfig = new HashMap<>();   
        for (int i = 0; i < playerCards.size(); i++)
        {  
            UUID id = playerIds.get(i);
            List<E_Card> cards = playerCards.get(i);
            
            List<E_CardStack> cardStacks = new ArrayList<>();                        
            addCardStack(cardStacks, SolShowCardStackType.SPECIAL, 0, cards.subList(0, 13), true); 
            addCardStack(cardStacks, SolShowCardStackType.MIDDLE, 0, cards.subList(13, 14), true);
            addCardStack(cardStacks, SolShowCardStackType.MIDDLE, 1, cards.subList(14, 15), true);
            addCardStack(cardStacks, SolShowCardStackType.MIDDLE, 2, cards.subList(15, 16), true);
            addCardStack(cardStacks, SolShowCardStackType.MIDDLE, 3, cards.subList(16, 17), true);
            addCardStack(cardStacks, SolShowCardStackType.DEPOT, 0, cards.subList(17, 52), false);
            addCardStack(cardStacks, SolShowCardStackType.TURNOVER, 0, new ArrayList<>(), false);
            addCardStack(cardStacks, SolShowCardStackType.LAYDOWN, 0, new ArrayList<>(), false);
            addCardStack(cardStacks, SolShowCardStackType.LAYDOWN, 1, new ArrayList<>(), false);
            addCardStack(cardStacks, SolShowCardStackType.LAYDOWN, 2, new ArrayList<>(), false);
            addCardStack(cardStacks, SolShowCardStackType.LAYDOWN, 3, new ArrayList<>(), false);
            
            playersCardConfig.put(id, cardStacks);
        }
        return playersCardConfig;
    }
    
    private void addCardStack(List<E_CardStack> listToAdd, String cardStackType, int typeNumber, List<E_Card> cards, boolean visible)
    {
        E_CardStack cs = E_CardStack.createCardStack(cards, cardStackType, typeNumber);
        for (E_Card card : cards)
        {
            card.setVisible(visible);
        }        
        listToAdd.add(cs);
    }

    @Override
    public List<List<E_Card>> getCards() {
        // solitaire showdown: each player starts with his own deck of 52 cards.        
        E_Card[] deck1 = CardDeckUtils.createFullDeck();
        CardDeckUtils.shuffleDeck(deck1);
        E_Card[] deck2 = CardDeckUtils.createFullDeck();
        CardDeckUtils.shuffleDeck(deck2);
        
        List<List<E_Card>> playerCards = new ArrayList<>();

        playerCards.add(Arrays.asList(deck1));
        playerCards.add(Arrays.asList(deck2));
        
        return playerCards;
    }
  
}

