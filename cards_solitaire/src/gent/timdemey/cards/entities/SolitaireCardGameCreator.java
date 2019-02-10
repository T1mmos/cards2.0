package gent.timdemey.cards.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Preconditions;

public class SolitaireCardGameCreator implements ICardGameCreator {

    @Override
    public Map<UUID, List<E_CardStack>> createStacks(List<UUID> playerIds, List<List<E_Card>> playerCards) {
        Preconditions.checkNotNull(playerCards);
        Preconditions.checkNotNull(playerCards);
        Preconditions.checkArgument(playerCards.size() == 1);
        Preconditions.checkArgument(playerCards.size() == 1);
        
        UUID playerId = playerIds.get(0);
        List<E_Card> allCards = playerCards.get(0);
                
        // stacks  
        List<E_CardStack> stacks = new ArrayList<>();
        {   // depot stack - all remaining cards = 52 - 28 = 24 cards
            List<E_Card> cards = allCards.subList(0, 24);
            cards.forEach(c -> c.setVisible(false));
            E_CardStack stack = E_CardStack.createCardStack(cards, SolitaireCardStackType.DEPOT, 0);
            stacks.add(stack);
        }
        {   // middle stacks - 1,2,...,7 cards so 1+2+...+7 = 7 * 8 / 2 = 28 cards in total
            for (int i = 0; i < 7; i++)
            {
                int skipCards = (i*(i+1))/2;
                int start = 24 + skipCards;
                int end = 24 + skipCards + i + 1;
                List<E_Card> cards = allCards.subList(start, end);
                allCards.subList(start, end - 1).forEach(c -> c.setVisible(false));
                E_CardStack stack = E_CardStack.createCardStack(cards, SolitaireCardStackType.MIDDLE, i);
                stacks.add(stack);                
            }            
        }
        {   // turnover stack - initially empty
            E_CardStack stack =  E_CardStack.createCardStack(new ArrayList<>(), SolitaireCardStackType.TURNOVER, 0);
            stacks.add(stack);                    
        }
        {   // laydown stacks - initially empty           
            for (int i = 0; i < 4; i++)
            {
                E_CardStack stack = E_CardStack.createCardStack(new ArrayList<>(), SolitaireCardStackType.LAYDOWN, i);
                stacks.add(stack);     
            }                 
        }
        
        Map<UUID, List<E_CardStack>> playerMap = new HashMap<>();
        playerMap.put(playerId, stacks);
        return playerMap;
    }

    @Override
    public List<List<E_Card>> getCards() {
        E_Card[] cards = CardDeckUtils.createFullDeck();
        CardDeckUtils.shuffleDeck(cards);
        List<List<E_Card>> playerCards = new ArrayList<>();
        playerCards.add(Arrays.asList(cards));
        return playerCards;
    }
}
