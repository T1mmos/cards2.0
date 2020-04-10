package gent.timdemey.cards.test.helpers;

import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.utils.CardDeckUtils;

public class CardDeckHelper
{
    private CardDeckHelper() {}
    
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
}
