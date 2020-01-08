package gent.timdemey.cards.model.cards;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import gent.timdemey.cards.model.state.State;

public final class CardDeckUtils {

    private CardDeckUtils() {

    }

    /**
     * Creates a deck of all 52 cards.
     * @return
     */
    public static Card[] createFullDeck()
    {
        return createDeck(Suit.values(), Value.values());
    }
    
    /**
     * Creates a deck with 32 cards - all suits with values 7, 8, 9, 10, 
     * J, Q, K and A. Can be used with Manillen.
     * @return
     */
    public static Card[] create7toADeck()
    {
        Value [] values = new Value[8];
        values[0] = Value.V_7;
        values[1] = Value.V_8;
        values[2] = Value.V_9;
        values[3] = Value.V_10;
        values[4] = Value.V_J;
        values[5] = Value.V_Q;
        values[6] = Value.V_K;
        values[7] = Value.V_A;
        return createDeck(Suit.values(), values);
    }
    
    public static Card[] createDeck(Suit [] suits, Value [] values)
    {
        Card[] cards = new Card[values.length * suits.length];
        int i = 0;
        for (Suit suit : Suit.values())
        {
            for (Value value : values)
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
        for (int i = deck.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            Card a = deck[index];
            deck[index] = deck[i];
            deck[i] = a;
        }
    }
}
