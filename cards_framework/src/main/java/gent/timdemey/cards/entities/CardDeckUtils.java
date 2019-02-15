package gent.timdemey.cards.entities;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class CardDeckUtils {

    private CardDeckUtils() {

    }

    /**
     * Creates a deck of all 52 cards.
     * @return
     */
    public static E_Card[] createFullDeck()
    {
        return createDeck(E_CardSuit.values(), E_CardValue.values());
    }
    
    /**
     * Creates a deck with 32 cards - all suits with values 7, 8, 9, 10, 
     * J, Q, K and A. Can be used with Manillen.
     * @return
     */
    public static E_Card[] create7toADeck()
    {
        E_CardValue [] values = new E_CardValue[8];
        values[0] = E_CardValue.V_7;
        values[1] = E_CardValue.V_8;
        values[2] = E_CardValue.V_9;
        values[3] = E_CardValue.V_10;
        values[4] = E_CardValue.V_J;
        values[5] = E_CardValue.V_Q;
        values[6] = E_CardValue.V_K;
        values[7] = E_CardValue.V_A;
        return createDeck(E_CardSuit.values(), values);
    }
    
    public static E_Card[] createDeck(E_CardSuit [] suits, E_CardValue [] values)
    {
        E_Card[] cards = new E_Card[values.length * suits.length];
        int i = 0;
        for (E_CardSuit suit : E_CardSuit.values())
        {
            for (E_CardValue value : values)
            {
                E_Card card = new E_Card(suit, value, true);
                cards[i++] = card;
            }
        }
        return cards;
    }    
    
    // Implementing Fisher-Yates shuffle
    public static void shuffleDeck(E_Card[] deck)
    {
        // If running on Java 6 or older, use `new Random()` on RHS here
        Random rnd = ThreadLocalRandom.current();
        for (int i = deck.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            E_Card a = deck[index];
            deck[index] = deck[i];
            deck[i] = a;
        }
    }
}
