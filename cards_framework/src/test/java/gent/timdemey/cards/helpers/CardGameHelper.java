package gent.timdemey.cards.helpers;

import gent.timdemey.cards.model.entities.state.Card;
import gent.timdemey.cards.model.entities.state.CardGame;
import gent.timdemey.cards.model.entities.state.CardStack;
import gent.timdemey.cards.model.entities.state.PlayerConfiguration;
import gent.timdemey.cards.equality.CardEquality;
import gent.timdemey.cards.equality.CardGameEquality;
import gent.timdemey.cards.equality.CardStackEquality;
import gent.timdemey.cards.equality.PlayerConfigurationEquality;
import gent.timdemey.cards.model.entities.state.CardOrder;
import gent.timdemey.cards.model.entities.state.CardSuit;
import gent.timdemey.cards.model.entities.state.CardValue;
import gent.timdemey.cards.model.entities.state.StateFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CardGameHelper
{

    private final StateFactory _StateFactory;
    
    public CardGameHelper (StateFactory stateFactory) 
    {
        this._StateFactory = stateFactory;
    }
    
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
    
    public CardGame CreateCardGame()
    {
        List<PlayerConfiguration> pCfgs = new ArrayList<>();
        pCfgs.add(CreatePlayerConfiguration(3, 3));
        pCfgs.add(CreatePlayerConfiguration(3, 3));
        CardGame cg = _StateFactory.CreateCardGame(pCfgs);
        return cg;        
    }
    
    public PlayerConfiguration CreatePlayerConfiguration(int nrOfCardStacks, int nrOfCardsPerStack)
    {
        List<CardStack> cardStacks = new ArrayList<>();
        for (int i = 0; i < nrOfCardStacks; i++)
        {
            List<Card> cards = new ArrayList<>();
            for (int j = 0; j < nrOfCardsPerStack; j++)
            {
                cards.add(_StateFactory.CreateCard(CardSuit.CLUBS, CardValue.getCardValue(CardOrder.AceToKing, j), true));
            }
            cardStacks.add(_StateFactory.CreateCardStack("test" + i, i, cards));
        }
        
        PlayerConfiguration pCfg = _StateFactory.CreatePlayerConfiguration(UUID.randomUUID(), cardStacks);
        return pCfg;
    }
}
