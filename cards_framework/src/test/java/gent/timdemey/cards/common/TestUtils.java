package gent.timdemey.cards.common;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestUtils
{    
    public static <X> void assertAreEqual(List<X> list1, List<X> list2, IEquality<X> equality)
    {
        if (list1 == null || list2 == null)
        {
            assertTrue(list1 == list2);
        }
        
        assertEquals(list1.size(), list2.size());
        
        for (int i = 0; i < list1.size(); i++)
        {
            X x1 = list1.get(i);
            X x2 = list2.get(i);
            assertAreEqual(x1, x2, equality);
        }
    }
    
    public static <X> void assertAreEqual(X x1, X x2, IEquality<X> comparer)
    {
        comparer.checkEquality(x1, x2);
    }
    
    
}
