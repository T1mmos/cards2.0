package gent.timdemey.cards.unit;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.mock.MockCardPlugin;
import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.di.DIException;
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
        c.AddTransient(IDIInterface.class, DIImplementation.class);
        
        // test transient, must return different object every time
        IDIInterface instance = c.Get(IDIInterface.class);
        assertEquals(DIImplementation.class, instance.getClass());
        IDIInterface instance2 = c.Get(IDIInterface.class);
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
        cP.AddTransient(IDIInterface.class, DIImplementation.class);
        
        // create child container, add extra mapping
        Container cC = cP.Scope();        
        cC.AddTransient(IDIInterface2.class, DIImplementation2.class);
        
        // test transient, must return different object every time
        IDIInterface instance = cC.Get(IDIInterface.class);
        assertEquals(DIImplementation.class, instance.getClass());
        IDIInterface instance2 = cC.Get(IDIInterface.class);
        assertNotEquals(instance2.hashCode(), instance.hashCode());
        IDIInterface instance6 = cP.Get(IDIInterface.class);
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
        IDIInterface2 instance3 = cC.Get(IDIInterface2.class);
        assertEquals(DIImplementation2.class, instance3.getClass());
        IDIInterface2 instance4 = cC.Get(IDIInterface2.class);
        assertNotEquals(instance4.hashCode(), instance3.hashCode());
        assertThrows(DIException.class, () -> cP.Get(IDIInterface2.class) );        
    }
    
    @Test
    public void testContainer()
    {
        Container c = new Container();
        c.AddSingleton(ICardPlugin.class, MockCardPlugin.class);
        c.AddTransient(IDIInterface.class, DIImplementation.class);
        
        IDIInterface testDI = c.Get(IDIInterface.class);
    }
}
