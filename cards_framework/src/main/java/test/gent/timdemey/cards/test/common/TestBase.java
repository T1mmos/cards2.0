package gent.timdemey.cards.test.common;

import org.junit.BeforeClass;

import gent.timdemey.cards.App;
import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.MockCardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.INetworkService;
import gent.timdemey.cards.services.context.ContextService;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.test.mock.MockNetworkService;

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
        IContextService ctxtService = new ContextService()
        {
            @Override
            public boolean isUiThread()
            {
                return true;
            }
        };
        ctxtService.initialize(ContextType.UI);
        App.getServices().install(IContextService.class, ctxtService);
        App.getServices().install(INetworkService.class, new MockNetworkService());
    }

    public static void installMockCardPlugin()
    {
        ICardPlugin plugin = new MockCardPlugin();
        App.getServices().install(ICardPlugin.class, plugin);
    }
}
