package gent.timdemey.cards.test.unittests;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.MockCardPlugin;
import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.di.ContainerBuilder;

import gent.timdemey.cards.test.common.TestBase;
import gent.timdemey.cards.test.mock.ITestDI;
import gent.timdemey.cards.test.mock.TestDI;
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
        ContainerBuilder cb = new ContainerBuilder();
        cb.AddSingleton(ICardPlugin.class, MockCardPlugin.class);
        cb.AddTransient(ITestDI.class, TestDI.class);
        Container c = cb.Build();
        
        ITestDI testDI = c.Get(ITestDI.class);
    }
   
}
