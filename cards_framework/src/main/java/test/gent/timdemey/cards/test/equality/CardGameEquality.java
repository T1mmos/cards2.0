package gent.timdemey.cards.test.equality;

import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.test.common.IEquality;
import gent.timdemey.cards.test.common.TestUtils;

public class CardGameEquality implements IEquality<CardGame>
{

    @Override
    public void checkEquality(CardGame x1, CardGame x2)
    {
        new EntityEquality().checkEquality(x1, x2);
        
        TestUtils.AssertAreEqual(x1.playerConfigurations, x2.playerConfigurations, new PlayerConfigurationEquality());        
    }

}
