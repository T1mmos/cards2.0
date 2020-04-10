package gent.timdemey.cards.test.unittests;

import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.SolShowPlugin;
import gent.timdemey.cards.model.commands.C_SolShowMove;
import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.CommandHistory;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.test.helpers.PlayerHelper;
import gent.timdemey.cards.test.helpers.SolShowCardGameHelper;
import gent.timdemey.cards.test.helpers.SolShowCardStackIdHelper;
import gent.timdemey.cards.test.mocks.MockContextService;

public class CommandHistoryUnitTest
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
    public void test()
    {
        Player player1 = PlayerHelper.getFixedPlayer(0);
        Player player2 = PlayerHelper.getFixedPlayer(1);
        CardGame cardGame = SolShowCardGameHelper.createFixedSolShowCardGame(player1, player2);
        CommandHistory cmdHistory = new CommandHistory(false, true);
        
        State state = new State();
        state.getPlayers().add(player1);
        state.getPlayers().add(player2);
        state.setCardGame(cardGame);
        // state.setCommandHistory(cmdHistory);
        
        cardGame.getCardStack(SolShowCardStackIdHelper.P1_DEPOT);
        
     /*   C_SolShowMove cmd = new C_SolShowMove(srcCardStackId, dstCardStackId, cardId)
        cmdHistory.addAwaiting(cmd, state);
        
        cmdHistory.addAwaiting(cmd, state);
        cmdHistory.accept(commandId);(commandExecution, state);*/
        
        fail("Not yet implemented");
    }

}
