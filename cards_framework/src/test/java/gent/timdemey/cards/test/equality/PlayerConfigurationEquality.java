package gent.timdemey.cards.test.equality;

import gent.timdemey.cards.model.entities.cards.PlayerConfiguration;
import gent.timdemey.cards.test.common.IEquality;
import gent.timdemey.cards.test.common.TestUtils;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerConfigurationEquality implements IEquality<PlayerConfiguration>
{

    @Override
    public void checkEquality(PlayerConfiguration x1, PlayerConfiguration x2)
    {
        new EntityEquality().checkEquality(x1, x2);
        
        assertEquals(x1.playerId, x2.playerId);
        TestUtils.assertAreEqual(x1.cardStacks, x2.cardStacks, new CardStackEquality());
    }

}
