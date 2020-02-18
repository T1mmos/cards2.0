package gent.timdemey.cards.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.model.cards.Card;
import gent.timdemey.cards.model.cards.CardDeckUtils;
import gent.timdemey.cards.model.cards.CardStack;
import gent.timdemey.cards.services.ICardGameCreationService;

public class SolitaireCardGameCreationService implements ICardGameCreationService
{
    @Override
    public Map<UUID, List<CardStack>> createStacks(List<UUID> playerIds, List<List<Card>> playerCards)
    {
        Preconditions.checkNotNull(playerCards);
        Preconditions.checkNotNull(playerCards);
        Preconditions.checkArgument(playerCards.size() == 1);
        Preconditions.checkArgument(playerCards.size() == 1);

        UUID playerId = playerIds.get(0);
        List<Card> allCards = playerCards.get(0);

        // stacks
        List<CardStack> stacks = new ArrayList<>();
        { // depot stack - all remaining cards = 52 - 28 = 24 cards
            List<Card> cards = allCards.subList(0, 24);
            cards.forEach(c -> c.visibleRef.set(false));
            CardStack stack = new CardStack(SolitaireCardStackType.DEPOT, 0);
            stack.cards.addAll(cards);
            for(Card card : cards)
            {
                card.cardStack = stack;
            }
            stacks.add(stack);
        }
        { // middle stacks - 1,2,...,7 cards so 1+2+...+7 = 7 * 8 / 2 = 28 cards in total
            for (int i = 0; i < 7; i++)
            {
                int skipCards = (i * (i + 1)) / 2;
                int start = 24 + skipCards;
                int end = 24 + skipCards + i + 1;
                List<Card> cards = allCards.subList(start, end);
                allCards.subList(start, end - 1).forEach(c -> c.visibleRef.set(false));
                CardStack stack = new CardStack(SolitaireCardStackType.MIDDLE, i);
                stack.cards.addAll(cards);
                for(Card card : cards)
                {
                    card.cardStack = stack;
                }
                stacks.add(stack);
            }
        }
        { // turnover stack - initially empty
            CardStack stack = new CardStack(SolitaireCardStackType.TURNOVER, 0);            
            stacks.add(stack);
        }
        { // laydown stacks - initially empty
            for (int i = 0; i < 4; i++)
            {
                CardStack stack = new CardStack(SolitaireCardStackType.LAYDOWN, i);
                stacks.add(stack);
            }
        }

        Map<UUID, List<CardStack>> playerMap = new HashMap<>();
        playerMap.put(playerId, stacks);
        return playerMap;
    }

    @Override
    public List<List<Card>> getCards()
    {
        Card[] cards = CardDeckUtils.createFullDeck();
        CardDeckUtils.shuffleDeck(cards);
        List<List<Card>> playerCards = new ArrayList<>();
        playerCards.add(Arrays.asList(cards));
        return playerCards;
    }
}
