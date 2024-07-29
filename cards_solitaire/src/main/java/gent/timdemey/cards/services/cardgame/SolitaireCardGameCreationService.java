package gent.timdemey.cards.services.cardgame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.state.Card;
import gent.timdemey.cards.model.entities.state.CardGame;
import gent.timdemey.cards.model.entities.state.CardStack;
import gent.timdemey.cards.model.entities.state.PlayerConfiguration;
import gent.timdemey.cards.model.entities.state.StateFactory;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyList;
import gent.timdemey.cards.services.contract.descriptors.SolitaireComponentTypes;
import gent.timdemey.cards.services.interfaces.ICardGameService;
import gent.timdemey.cards.utils.CardDeckUtils;

public class SolitaireCardGameCreationService implements ICardGameService
{

    private final StateFactory _StateFactory;
    private final CardDeckUtils _CardDeckUtils;
    public SolitaireCardGameCreationService(StateFactory stateFactory, CardDeckUtils cardDeckUtils)
    {
        this._StateFactory = stateFactory;
        this._CardDeckUtils = cardDeckUtils;
    }
    
    @Override
    public CardGame createCardGame(List<UUID> playerIds)
    {
        List<List<Card>> playerCards = getCards();
        
        if (playerIds == null)
        {
            throw new IllegalArgumentException("playerIds");
        }
        if (playerIds.size() != 1)
        {
            throw new IllegalArgumentException("playerIds.size is " + playerIds.size());
        }
        if (playerCards == null)
        {
            throw new IllegalArgumentException("playerCards");
        }
        if (playerCards.size() != 1)
        {
            throw new IllegalArgumentException("playerCards.size is " + playerCards.size());
        }

        UUID playerId = playerIds.get(0);
        List<Card> allCards = playerCards.get(0);

        // stacks
        List<CardStack> stacks = new ArrayList<>();
        { // depot stack - all remaining cards = 52 - 28 = 24 cards
            List<Card> cards = allCards.subList(0, 24);
            cards.forEach(c -> c.visibleRef.set(false));
            CardStack stack = _StateFactory.CreateCardStack(SolitaireComponentTypes.DEPOT, 0);
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
                CardStack stack = _StateFactory.CreateCardStack(SolitaireComponentTypes.MIDDLE, i);
                stack.cards.addAll(cards);
                for(Card card : cards)
                {
                    card.cardStack = stack;
                }
                stacks.add(stack);
            }
        }
        { // turnover stack - initially empty
            CardStack stack = _StateFactory.CreateCardStack(SolitaireComponentTypes.TURNOVER, 0);            
            stacks.add(stack);
        }
        { // laydown stacks - initially empty
            for (int i = 0; i < 4; i++)
            {
                CardStack stack = _StateFactory.CreateCardStack(SolitaireComponentTypes.LAYDOWN, i);
                stacks.add(stack);
            }
        }

        PlayerConfiguration pc = _StateFactory.CreatePlayerConfiguration(playerId, stacks);
        List<PlayerConfiguration> pcs = new ArrayList<>();
        pcs.add(pc);
        
        CardGame cg = _StateFactory.CreateCardGame(pcs);
        return cg;
    }

    private List<List<Card>> getCards()
    {
        Card[] cards = _CardDeckUtils.createFullDeck();
        CardDeckUtils.shuffleDeck(cards);
        List<List<Card>> playerCards = new ArrayList<>();
        playerCards.add(Arrays.asList(cards));
        return playerCards;
    }

    @Override
    public int getScore(ReadOnlyCardStack srcCardStack, ReadOnlyCardStack dstCardStack, ReadOnlyList<ReadOnlyCard> transferedCards)
    {
        return 0; // currently no score in Solitaire
    }
}
