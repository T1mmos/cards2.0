package gent.timdemey.cards.services.cardgame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.cards.CardStack;
import gent.timdemey.cards.model.entities.cards.PlayerConfiguration;
import gent.timdemey.cards.services.ICardGameCreationService;
import gent.timdemey.cards.utils.CardDeckUtils;

public class SolShowCardGameCreationService implements ICardGameCreationService
{
    @Override
    public List<List<Card>> getCards()
    {
        // solitaire showdown: each player starts with his own deck of 52 cards.
        Card[] deck1 = CardDeckUtils.createFullDeck();
        CardDeckUtils.shuffleDeck(deck1);
        Card[] deck2 = CardDeckUtils.createFullDeck();
        CardDeckUtils.shuffleDeck(deck2);

        List<List<Card>> playerCards = new ArrayList<>();

        playerCards.add(Arrays.asList(deck1));
        playerCards.add(Arrays.asList(deck2));

        return playerCards;
    }

    @Override
    public List<PlayerConfiguration> createStacks(List<UUID> playerIds, List<List<Card>> playerCards)
    {
        List<PlayerConfiguration> playersConfiguration = new ArrayList<>();
        for (int i = 0; i < playerCards.size(); i++)
        {
            UUID id = playerIds.get(i);
            List<Card> cards = playerCards.get(i);

            List<CardStack> cardStacks = new ArrayList<>();
            addCardStack(cardStacks, SolShowCardStackType.SPECIAL, 0, cards.subList(0, 13), false);
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

            PlayerConfiguration pc = new PlayerConfiguration(id, cardStacks);
            playersConfiguration.add(pc);
        }
        return playersConfiguration;
    }
    
    private void addCardStack(List<CardStack> listToAdd, String cardStackType, int typeNumber, List<Card> cards, boolean visible)
    {
        CardStack cs = new CardStack(cardStackType, typeNumber);
        cs.cards.addAll(cards);
        for (Card card : cards)
        {
            card.cardStack = cs;
            card.visibleRef.set(visible);            
        }
        
        if (cardStackType.equals(SolShowCardStackType.SPECIAL))
        {
            // highest card is visible
            cs.getHighestCard().visibleRef.set(true);
        }
        
        listToAdd.add(cs);
    }
}
