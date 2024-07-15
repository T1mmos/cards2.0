package gent.timdemey.cards.test.common;

import java.util.List;
import java.util.UUID;
import gent.timdemey.cards.App;
import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.MockCardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.model.entities.cards.CardGame;
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

import org.junit.jupiter.api.BeforeAll;

public class TestBase
{
    @BeforeAll
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
        
        ICardGameService cardGameServ = new ICardGameService()
        {
            
            @Override
            public int getScore(ReadOnlyCardStack srcCardStack, ReadOnlyCardStack dstCardStack, ReadOnlyList<ReadOnlyCard> transferedCards)
            {
                return 0;
            }
            
            @Override
            public CardGame createCardGame(List<UUID> playerIds)
            {
                return null;
            }
        };
        
        ctxtService.initialize(ContextType.UI);
        App.getServices().installIfAbsent(IContextService.class, () -> ctxtService);
        App.getServices().installIfAbsent(INetworkService.class, () -> new MockNetworkService());
        App.getServices().installIfAbsent(ILogManager.class, () -> new MockLogManager()); 
        App.getServices().installIfAbsent(ICardGameService.class, () -> cardGameServ);
    }

    public static void installMockCardPlugin()
    {
        ICardPlugin plugin = new MockCardPlugin();
        App.getServices().install(ICardPlugin.class, plugin);
    }
}
