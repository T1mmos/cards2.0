package gent.timdemey.cards.test.helpers;

import gent.timdemey.cards.model.entities.state.Card;
import gent.timdemey.cards.model.entities.state.CardStack;
import gent.timdemey.cards.model.entities.state.CardSuit;
import gent.timdemey.cards.model.entities.state.CardValue;

public class CardStackHelper
{
    public static CardStack createCardStack()
    {
        CardStack cs = new CardStack("UnitTest-cardStackType", 7);
        for (int i = 0; i < 10; i++)
        {
            CardSuit suit = CardSuit.values()[i % 4];
            CardValue value = CardValue.values()[i % 13];
            Card card = new Card(suit, value, true);
            cs.cards.add(card);
        }
        
        return cs;
    }
}
