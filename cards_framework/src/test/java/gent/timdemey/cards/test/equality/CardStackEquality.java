package gent.timdemey.cards.test.equality;

import gent.timdemey.cards.model.entities.state.CardStack;
import gent.timdemey.cards.test.common.IEquality;
import gent.timdemey.cards.test.common.TestUtils;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardStackEquality implements IEquality<CardStack>
{

    @Override
    public void checkEquality(CardStack x1, CardStack x2)
    {
        new EntityEquality().checkEquality(x1, x2);
        
        assertEquals(x1.cardStackType, x2.cardStackType);
        assertEquals(x1.typeNumber, x2.typeNumber);        
        TestUtils.assertAreEqual(x1.cards, x2.cards, new CardEquality());
    }

}
