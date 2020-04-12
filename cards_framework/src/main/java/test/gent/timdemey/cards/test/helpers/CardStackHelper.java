package gent.timdemey.cards.test.helpers;

import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.cards.CardStack;
import gent.timdemey.cards.model.entities.cards.Suit;
import gent.timdemey.cards.model.entities.cards.Value;

public class CardStackHelper
{
    public static CardStack createCardStack()
    {
        CardStack cs = new CardStack("UnitTest-cardStackType", 7);
        for (int i = 0; i < 10; i++)
        {
            Suit suit = Suit.values()[i % 4];
            Value value = Value.values()[i % 13];
            Card card = new Card(suit, value, true);
            cs.cards.add(card);
        }
        
        return cs;
    }
}
