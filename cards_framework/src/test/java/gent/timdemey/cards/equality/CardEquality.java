package gent.timdemey.cards.equality;

import gent.timdemey.cards.model.entities.state.Card;
import gent.timdemey.cards.common.IEquality;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardEquality implements IEquality<Card>
{

    @Override
    public void checkEquality(Card c1, Card c2)
    {
        new EntityEquality().checkEquality(c1, c2);
        
        assertEquals(c1.cardStack.id, c2.cardStack.id);
        assertEquals(c1.suit, c2.suit);
        assertEquals(c1.value, c2.value);
        assertEquals(c1.visibleRef.get(), c2.visibleRef.get());
    }

}
