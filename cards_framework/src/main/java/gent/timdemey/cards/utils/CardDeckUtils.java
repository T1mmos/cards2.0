package gent.timdemey.cards.utils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import gent.timdemey.cards.model.entities.state.Card;
import gent.timdemey.cards.model.entities.state.CardSuit;
import gent.timdemey.cards.model.entities.state.CardValue;

public final class CardDeckUtils
{
    private CardDeckUtils()
    {

    }

    /**
     * Creates a deck of all 52 cards.
     * 
     * @return
     */
    public static Card[] createFullDeck()
    {
        return createDeck(CardSuit.values(), CardValue.values());
    }

    /**
     * Creates a deck with 32 cards - all suits with values 7, 8, 9, 10, J, Q, K and
     * A. Can be used with Manillen.
     * 
     * @return
     */
    public static Card[] create7toADeck()
    {
        CardValue[] values = new CardValue[8];
        values[0] = CardValue.V_7;
        values[1] = CardValue.V_8;
        values[2] = CardValue.V_9;
        values[3] = CardValue.V_10;
        values[4] = CardValue.V_J;
        values[5] = CardValue.V_Q;
        values[6] = CardValue.V_K;
        values[7] = CardValue.V_A;
        return createDeck(CardSuit.values(), values);
    }

    public static Card[] createDeck(CardSuit[] suits, CardValue[] values)
    {
        Card[] cards = new Card[values.length * suits.length];
        int i = 0;
        for (CardSuit suit : CardSuit.values())
        {
            for (CardValue value : values)
            {
                Card card = new Card(suit, value, true);
                cards[i++] = card;
            }
        }
        return cards;
    }

    // Implementing Fisher-Yates shuffle
    public static void shuffleDeck(Card[] deck)
    {
        // If running on Java 6 or older, use `new Random()` on RHS here
        Random rnd = ThreadLocalRandom.current();
        for (int i = deck.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            Card a = deck[index];
            deck[index] = deck[i];
            deck[i] = a;
        }
    }
}
