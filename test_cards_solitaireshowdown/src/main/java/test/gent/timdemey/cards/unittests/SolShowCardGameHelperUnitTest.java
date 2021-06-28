package gent.timdemey.cards.unittests;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;

import gent.timdemey.cards.common.SolShowTestBase;
import gent.timdemey.cards.helpers.PlayerHelper;
import gent.timdemey.cards.helpers.SolShowTestIds;
import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.cards.CardStack;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.services.cardgame.SolShowCardStackType;
import gent.timdemey.cards.services.cardgame.SolShowTestCardGameService;
import gent.timdemey.cards.services.interfaces.ICardGameService;
import gent.timdemey.cards.test.helpers.CardGameHelper;

public class SolShowCardGameHelperUnitTest extends SolShowTestBase
{
    @BeforeClass
    public static void init()
    {
        installSolShowCardPlugin();
        installMockContextService();     
    }
    
    @Test
    public void checkFixedGame()
    {
        CardGame cg1 = createFixedSolShowCardGame();
        CardGame cg2 = createFixedSolShowCardGame();
        
        // check always the same game
        CardGameHelper.assertEquals(cg1, cg2);
        
        // check some card stacks, they should have the fixed ID's from SolShowCardStackIdHelper
        {            
            CardStack cs = cg1.getCardStack(SolShowTestIds.P1_DEPOT);
            assertEquals(SolShowCardStackType.DEPOT, cs.cardStackType);
            assertEquals(0, cs.typeNumber);
            assertEquals(SolShowTestIds.P1_DEPOT, cs.id);
            
            cg1.playerConfigurations.get(0).cardStacks.contains(cs);
        }
        {
            CardStack cs = cg1.getCardStack(SolShowTestIds.P2_LAYDOWN1);
            assertEquals(SolShowCardStackType.LAYDOWN, cs.cardStackType);
            assertEquals(0, cs.typeNumber);
            assertEquals(SolShowTestIds.P2_LAYDOWN1, cs.id);
            
            cg1.playerConfigurations.get(1).cardStacks.contains(cs);
        }
    }
    
    @Test
    public void printFixedGame()
    {
        CardGame cg = createFixedSolShowCardGame();
        
        for (CardStack cs : cg.getCardStacks())
        {
            System.out.println(cs);
        }
    }

    private CardGame createFixedSolShowCardGame()
    {
        Player player1 = PlayerHelper.getFixedPlayer(0);
        Player player2 = PlayerHelper.getFixedPlayer(1);
        List<UUID> playerIds = Arrays.asList(player1.id, player2.id);
        
        ICardGameService cgServ = new SolShowTestCardGameService();
        CardGame cg = cgServ.createCardGame(playerIds);
        return cg;
    }
}
