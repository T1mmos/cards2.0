package gent.timdemey.cards.unittests;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.mock.MockCardPlugin;
import gent.timdemey.cards.di.Container;

import gent.timdemey.cards.common.TestBase;
import gent.timdemey.cards.mock.ITestDI;
import gent.timdemey.cards.mock.TestDI;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class UtilsUnitTest extends TestBase
{
    @BeforeAll
    public static void init()
    {
  //      installMockCardPlugin();
//        installMockContextService();
    }

    @Test
    public void testContainer()
    {
        Container c = new Container();
        c.AddSingleton(ICardPlugin.class, MockCardPlugin.class);
        c.AddTransient(ITestDI.class, TestDI.class);
        
        ITestDI testDI = c.Get(ITestDI.class);
    }
   
}
