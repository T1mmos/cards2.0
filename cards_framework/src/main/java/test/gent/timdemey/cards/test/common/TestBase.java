package gent.timdemey.cards.test.common;

import java.util.List;
import java.util.UUID;

import org.junit.BeforeClass;

import gent.timdemey.cards.App;
import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.MockCardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.cards.PlayerConfiguration;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyList;
import gent.timdemey.cards.services.context.ContextService;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.ICardGameService;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.INetworkService;
import gent.timdemey.cards.test.mock.MockLogManager;
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
        App.getServices().installIfAbsent(IContextService.class, () -> ctxtService);
        App.getServices().installIfAbsent(INetworkService.class,  () -> new MockNetworkService());
        App.getServices().installIfAbsent(ILogManager.class,  () -> new MockLogManager());
        App.getServices().installIfAbsent(ICardGameService.class,  () -> new ICardGameService()
        {
            
            @Override
            public int getScore(ReadOnlyCardStack srcCardStack, ReadOnlyCardStack dstCardStack, ReadOnlyList<ReadOnlyCard> transferedCards)
            {
                // TODO Auto-generated method stub
                return 0;
            }
            
            @Override
            public List<List<Card>> getCards()
            {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public List<PlayerConfiguration> createStacks(List<UUID> playerIds, List<List<Card>> playerCards)
            {
                // TODO Auto-generated method stub
                return null;
            }
        });
    }

    public static void installMockCardPlugin()
    {
        ICardPlugin plugin = new MockCardPlugin();
        App.getServices().install(ICardPlugin.class, plugin);
    }
}
