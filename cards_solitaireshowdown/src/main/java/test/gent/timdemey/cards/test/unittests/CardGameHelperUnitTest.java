package gent.timdemey.cards.test.unittests;

import org.junit.BeforeClass;
import org.junit.Test;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.SolShowPlugin;
import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.test.helpers.CardGameHelper;
import gent.timdemey.cards.test.helpers.SolShowCardGameHelper;
import gent.timdemey.cards.test.mocks.MockContextService;

public class CardGameHelperUnitTest
{
    @BeforeClass
    public static void init()
    {

        Services.install(ICardPlugin.class, new SolShowPlugin());
        
        MockContextService ctxtService = new MockContextService();
        
        ctxtService.initialize(ContextType.UI);
        Services.install(IContextService.class, ctxtService);
        
    }
    
    @Test
    public void checkShuffledOrder()
    {
        CardGame cg1 = SolShowCardGameHelper.createFixedSolShowCardGame();
        CardGame cg2 = SolShowCardGameHelper.createFixedSolShowCardGame();
        
        CardGameHelper.assertEquals(cg1, cg2);
    }

}
