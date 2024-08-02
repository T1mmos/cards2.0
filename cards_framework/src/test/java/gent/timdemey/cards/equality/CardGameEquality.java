package gent.timdemey.cards.equality;

import gent.timdemey.cards.model.entities.state.CardGame;
import gent.timdemey.cards.common.IEquality;
import gent.timdemey.cards.common.TestUtils;

public class CardGameEquality implements IEquality<CardGame>
{

    @Override
    public void checkEquality(CardGame x1, CardGame x2)
    {
        new EntityEquality().checkEquality(x1, x2);
        
        TestUtils.assertAreEqual(x1.playerConfigurations, x2.playerConfigurations, new PlayerConfigurationEquality());        
    }

}
