package gent.timdemey.cards.test.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.MockCardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.context.MockContextService;

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
    
    public static void installMockContextService()
    {
        MockContextService ctxtService = new MockContextService();
        ctxtService.initialize(ContextType.UI);
        Services.install(IContextService.class, ctxtService);   
    }
    
    public static void installMockCardPlugin()
    {
        ICardPlugin plugin = new MockCardPlugin();
        Services.install(ICardPlugin.class, plugin);
    }
}
