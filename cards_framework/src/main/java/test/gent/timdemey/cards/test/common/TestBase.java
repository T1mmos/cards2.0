package gent.timdemey.cards.test.common;

import org.junit.BeforeClass;

import gent.timdemey.cards.App;
import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.MockCardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.context.MockContextService;

public class TestBase
{
    @BeforeClass
    public static void InitTestBase()
    {
        Services services = new Services();
        App.init(services);
    }
    
    public static void installMockContextService()
    {
        MockContextService ctxtService = new MockContextService();
        ctxtService.initialize(ContextType.UI);
        ctxtService.initialize(ContextType.Client);          
        App.getServices().install(IContextService.class, ctxtService);   
    }
    
    public static void installMockCardPlugin()
    {
        ICardPlugin plugin = new MockCardPlugin();
        App.getServices().install(ICardPlugin.class, plugin);
    }
}
