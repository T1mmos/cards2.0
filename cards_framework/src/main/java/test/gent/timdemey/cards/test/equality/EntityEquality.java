package gent.timdemey.cards.test.equality;

import static org.junit.Assert.assertEquals;

import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.test.common.IEquality;

public class EntityEquality implements IEquality<EntityBase>
{

    @Override
    public void checkEquality(EntityBase e1, EntityBase e2)
    {
        if (e1 == null || e2 == null)
        {
            assertEquals(e1, e2);
        }
        
        assertEquals(e1.id, e2.id);
    }
}
