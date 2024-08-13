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
    public void testContainer()
    {
        Container c = new Container();
        c.AddSingleton(ICardPlugin.class, MockCardPlugin.class);
        c.AddTransient(IDIInterface.class, DIImplementation.class);
        
        IDIInterface testDI = c.Get(IDIInterface.class);
    }
}
