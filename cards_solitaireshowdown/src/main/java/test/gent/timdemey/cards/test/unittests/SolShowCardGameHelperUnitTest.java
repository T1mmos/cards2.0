package gent.timdemey.cards.test.unittests;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.SolShowPlugin;
import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.cards.CardStack;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.boot.SolShowCardStackType;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.test.helpers.CardGameHelper;
import gent.timdemey.cards.test.helpers.PlayerHelper;
import gent.timdemey.cards.test.helpers.SolShowCardGameHelper;
import gent.timdemey.cards.test.helpers.SolShowCardStackIdHelper;
import gent.timdemey.cards.test.mocks.MockContextService;

public class SolShowCardGameHelperUnitTest
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
    public void checkFixedGame()
    {
        Player player1 = PlayerHelper.getFixedPlayer(0);
        Player player2 = PlayerHelper.getFixedPlayer(1);
        CardGame cg1 = SolShowCardGameHelper.createFixedSolShowCardGame(player1, player2);
        CardGame cg2 = SolShowCardGameHelper.createFixedSolShowCardGame(player1, player2);
        
        // check always the same game
        CardGameHelper.assertEquals(cg1, cg2);
        
        // check some card stacks, they should have the fixed ID's from SolShowCardStackIdHelper
        {            
            CardStack cs = cg1.getCardStack(SolShowCardStackIdHelper.P1_DEPOT);
            assertEquals(SolShowCardStackType.DEPOT, cs.cardStackType);
            assertEquals(0, cs.typeNumber);
            assertEquals(SolShowCardStackIdHelper.P1_DEPOT, cs.id);
            
            cg1.playerConfigurations.get(0).cardStacks.contains(cs);
        }
        {
            CardStack cs = cg1.getCardStack(SolShowCardStackIdHelper.P2_LAYDOWN1);
            assertEquals(SolShowCardStackType.LAYDOWN, cs.cardStackType);
            assertEquals(0, cs.typeNumber);
            assertEquals(SolShowCardStackIdHelper.P2_LAYDOWN1, cs.id);
            
            cg1.playerConfigurations.get(1).cardStacks.contains(cs);
        }
    }
}
