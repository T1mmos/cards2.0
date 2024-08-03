package gent.timdemey.cards.utils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import gent.timdemey.cards.model.entities.state.Card;

public class CardDeckUtils
{

    private CardDeckUtils()
    {
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
