package gent.timdemey.cards.test.unittests;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.MockCardPlugin;
import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.di.ContainerBuilder;
import gent.timdemey.cards.test.mock.ITestDI;
import gent.timdemey.cards.test.mock.TestDI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Timmos
 */
public class DITest {
    
    @Test
    public void testContainer()
    {
        ContainerBuilder cb = new ContainerBuilder();
        cb.AddSingleton(ICardPlugin.class, MockCardPlugin.class);
        cb.AddTransient(ITestDI.class, TestDI.class);
        Container c = cb.Build();
        
        // test transient, must return different object every time
        ITestDI instance = c.Get(ITestDI.class);
        assertEquals(TestDI.class, instance.getClass());
        ITestDI instance2 = c.Get(ITestDI.class);
        assertNotEquals(instance2.hashCode(), instance.hashCode());
        
        // test singleton, must return same object every time
        ICardPlugin singleton = c.Get(ICardPlugin.class);
        assertEquals(MockCardPlugin.class, singleton.getClass());        
        ICardPlugin singleton2 = c.Get(ICardPlugin.class);
        assertEquals(singleton2.hashCode(), singleton.hashCode());   
    }
    
}
