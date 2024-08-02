package gent.timdemey.cards.unittests;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.mock.MockCardPlugin;
import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.di.DIException;
import gent.timdemey.cards.mock.ITestDI;
import gent.timdemey.cards.mock.ITestDI2;
import gent.timdemey.cards.mock.TestDI;
import gent.timdemey.cards.mock.TestDI2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Timmos
 */
public class ContainerTests {
    
    @Test
    public void basic()
    {
        Container c = new Container();
        c.AddSingleton(ICardPlugin.class, MockCardPlugin.class);
        c.AddTransient(ITestDI.class, TestDI.class);
        
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
    
    @Test
    public void scope()
    {
        // create parent container
        Container cP = new Container();
        cP.AddSingleton(ICardPlugin.class, MockCardPlugin.class);
        cP.AddTransient(ITestDI.class, TestDI.class);
        
        // create child container, add extra mapping
        Container cC = cP.Scope();        
        cC.AddTransient(ITestDI2.class, TestDI2.class);
        
        // test transient, must return different object every time
        ITestDI instance = cC.Get(ITestDI.class);
        assertEquals(TestDI.class, instance.getClass());
        ITestDI instance2 = cC.Get(ITestDI.class);
        assertNotEquals(instance2.hashCode(), instance.hashCode());
        ITestDI instance6 = cP.Get(ITestDI.class);
        assertNotEquals(instance6.hashCode(), instance.hashCode());
        assertNotEquals(instance6.hashCode(), instance2.hashCode());
        
        // test singleton, must return same object every time, even from the child container
        ICardPlugin singleton = cC.Get(ICardPlugin.class);     // child
        assertEquals(MockCardPlugin.class, singleton.getClass());        
        ICardPlugin singleton2 = cC.Get(ICardPlugin.class);    // child
        assertEquals(singleton2.hashCode(), singleton.hashCode());   
        ICardPlugin singleton3 = cP.Get(ICardPlugin.class);          // parent
        assertEquals(singleton3.hashCode(), singleton.hashCode());   
        
        // test child-added interface - child has mapping but not the parent
        ITestDI2 instance3 = cC.Get(ITestDI2.class);
        assertEquals(TestDI2.class, instance3.getClass());
        ITestDI2 instance4 = cC.Get(ITestDI2.class);
        assertNotEquals(instance4.hashCode(), instance3.hashCode());
        assertThrows(DIException.class, () -> cP.Get(ITestDI2.class) );
        
    }
}
