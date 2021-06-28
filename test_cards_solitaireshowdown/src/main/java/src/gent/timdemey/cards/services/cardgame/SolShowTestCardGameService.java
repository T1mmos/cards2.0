package gent.timdemey.cards.services.cardgame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.cards.CardStack;
import gent.timdemey.cards.model.entities.cards.PlayerConfiguration;
import gent.timdemey.cards.model.entities.cards.payload.P_Card;
import gent.timdemey.cards.model.entities.cards.payload.P_CardGame;
import gent.timdemey.cards.model.entities.cards.payload.P_PlayerConfiguration;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyList;
import gent.timdemey.cards.services.interfaces.ICardGameService;
import gent.timdemey.cards.test.helpers.IdHelper;
import gent.timdemey.cards.utils.CardDeckUtils;

public class SolShowTestCardGameService implements ICardGameService
{
    public static Card[] createFixedDeck1()
    {
        int[][] swaps = new int[][]
        {
                { 16, 1 },
                { 12, 32 },
                { 34, 46 },
                { 21, 27 },
                { 14, 19 },
                { 41, 15 },
                { 33, 30 },
                { 0, 31 },
                { 51, 10 },
                { 45, 50 },
                { 25, 7 },
                { 40, 18 },
                { 9, 23 },
                { 49, 39 },
                { 11, 13 },
                { 44, 2 },
                { 35, 8 },
                { 6, 43 },
                { 3, 42 },
                { 26, 48 },
                { 4, 28 },
                { 29, 5 },
                { 22, 36 },
                { 47, 20 },
                { 38, 24 },
                { 37, 17 } };
        Card[] cards = CardDeckUtils.createFullDeck();
        shuffle(swaps, cards);
        return cards;
    }

    public static Card[] createFixedDeck2()
    {
        int[][] swaps = new int[][]
        {
                { 7, 17 },
                { 44, 40 },
                { 38, 39 },
                { 18, 13 },
                { 14, 31 },
                { 23, 27 },
                { 15, 4 },
                { 35, 21 },
                { 29, 5 },
                { 37, 1 },
                { 49, 2 },
                { 36, 33 },
                { 9, 16 },
                { 43, 50 },
                { 0, 47 },
                { 11, 32 },
                { 41, 22 },
                { 34, 30 },
                { 46, 45 },
                { 51, 12 },
                { 28, 26 },
                { 48, 3 },
                { 25, 19 },
                { 6, 10 },
                { 8, 20 },
                { 42, 24 } };
        Card[] cards = CardDeckUtils.createFullDeck();
        shuffle(swaps, cards);
        return cards;
    }
    
    private static void shuffle(int[][] swaps, Card[] cards)
    {
        for (int[] swap : swaps)
        {
            int idx1 = swap[0];
            int idx2 = swap[1];

            Card tmp = cards[idx1];
            cards[idx1] = cards[idx2];
            cards[idx2] = tmp;
        }
    }
    
    @Override
    public CardGame createCardGame(List<UUID> playerIds)
    {
        // create a cardgame where all entities have fixed UUIDs
        List<List<Card>> fixedDecks = Arrays.asList(Arrays.asList(createFixedDeck1()), Arrays.asList(createFixedDeck2()));
        
        // players
        List<PlayerConfiguration> playerConfigs = new ArrayList<>();
        for (int i = 0; i < 2; i++)
        {
            List<Card> deck = fixedDecks.get(i);
            P_PlayerConfiguration pPlayerConf = new P_PlayerConfiguration();
            pPlayerConf.id = IdHelper.createFixedPlayerConfigurationId(i);
            pPlayerConf.playerId = IdHelper.createFixedPlayerId(i);
            pPlayerConf.cardStacks = new ArrayList<>();
            
            addCardStack(pPlayerConf.cardStacks, i, SolShowCardStackType.SPECIAL, 0, deck, 0, 12, false);
            addCardStack(pPlayerConf.cardStacks, i, SolShowCardStackType.MIDDLE, 0, deck, 13, 13, true);
            addCardStack(pPlayerConf.cardStacks, i, SolShowCardStackType.MIDDLE, 1, deck, 14, 14, true);
            addCardStack(pPlayerConf.cardStacks, i, SolShowCardStackType.MIDDLE, 2, deck, 15, 15, true);
            addCardStack(pPlayerConf.cardStacks, i, SolShowCardStackType.MIDDLE, 3, deck, 16, 16, true);
            addCardStack(pPlayerConf.cardStacks, i, SolShowCardStackType.DEPOT, 0, deck, 17, 51, false);
            addCardStack(pPlayerConf.cardStacks, i, SolShowCardStackType.TURNOVER, 0, deck, 0, -1, false);
            addCardStack(pPlayerConf.cardStacks, i, SolShowCardStackType.LAYDOWN, 0, deck, 0, -1, false);
            addCardStack(pPlayerConf.cardStacks, i, SolShowCardStackType.LAYDOWN, 1, deck, 0, -1, false);
            addCardStack(pPlayerConf.cardStacks, i, SolShowCardStackType.LAYDOWN, 2, deck, 0, -1, false);
            addCardStack(pPlayerConf.cardStacks, i, SolShowCardStackType.LAYDOWN, 3, deck, 0, -1, false);
            
            PlayerConfiguration playerConfig = new PlayerConfiguration(pPlayerConf);
            playerConfigs.add(playerConfig);
        }
        
        // create cardgame
        P_CardGame pCg = new P_CardGame();
        pCg.id = IdHelper.createFixedCardGameId();
        pCg.playerConfigurations = playerConfigs;        
        
        return new CardGame(pCg);
    }
    
    private void addCardStack(List<CardStack> listToAdd, int playerNr, String cardStackType, int typeNumber, List<Card> deck, int start, int end, boolean visible)
    {
        CardStack cs = new CardStack(cardStackType, typeNumber);
        
        // recreate the cards, to assign fixed UUIDs
        List<Card> createdCards = new ArrayList<Card>();
        for (int j = start; j <= end; j++)
        {
            Card card_src = deck.get(j);
            P_Card pCard = new P_Card();
            pCard.id = IdHelper.createFixedCardId(playerNr, typeNumber, j);
            pCard.suit = card_src.suit;
            pCard.value = card_src.value;
            pCard.visible = visible;
                
            Card card_dst = new Card(pCard);
            card_dst.cardStack = cs;
            createdCards.add(card_dst);
        }
        cs.cards.addAll(createdCards);
              
        if (cardStackType.equals(SolShowCardStackType.SPECIAL))
        {
            // highest card is visible
            cs.getHighestCard().visibleRef.set(true);
        }
        
        listToAdd.add(cs);
    }
    
    @Override
    public int getScore(ReadOnlyCardStack srcCardStack, ReadOnlyCardStack dstCardStack,
            ReadOnlyList<ReadOnlyCard> transferedCards)
    {
        return 10;
    }
    
}
