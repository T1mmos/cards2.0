package gent.timdemey.cards.helpers;

import gent.timdemey.cards.model.entities.state.Card;
import gent.timdemey.cards.model.entities.state.CardGame;
import gent.timdemey.cards.model.entities.state.CardStack;
import gent.timdemey.cards.model.entities.state.PlayerConfiguration;
import gent.timdemey.cards.equality.CardEquality;
import gent.timdemey.cards.equality.CardGameEquality;
import gent.timdemey.cards.equality.CardStackEquality;
import gent.timdemey.cards.equality.PlayerConfigurationEquality;

public class CardGameHelper
{
    private CardGameHelper () {}
    
    public static void assertEquals(CardGame cg1, CardGame cg2)
    {
        new CardGameEquality().checkEquality(cg1, cg2);
    }
    
    public static void assertEquals(PlayerConfiguration pc1, PlayerConfiguration pc2)
    {
        new PlayerConfigurationEquality().checkEquality(pc1, pc2);
    }
    
    public static void assertEquals(CardStack cs1, CardStack cs2)
    {
        new CardStackEquality().checkEquality(cs1, cs2);
    }
    
    public static void assertEquals(Card c1, Card c2)
    {
        new CardEquality().checkEquality(c1, c2);
    }
}
