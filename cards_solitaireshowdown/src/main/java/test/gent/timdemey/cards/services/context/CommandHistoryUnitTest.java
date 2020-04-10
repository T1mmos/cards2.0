package gent.timdemey.cards.services.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.SolShowPlugin;
import gent.timdemey.cards.model.commands.C_SolShowMove;
import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.cards.CardStack;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.entities.game.Server;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.test.helpers.PlayerHelper;
import gent.timdemey.cards.test.helpers.ServerHelper;
import gent.timdemey.cards.test.helpers.SolShowCardGameHelper;
import gent.timdemey.cards.test.helpers.SolShowTestIds;
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
    public void awaitAndAccept()
    {
        Player player1 = PlayerHelper.getFixedPlayer(0);
        Player player2 = PlayerHelper.getFixedPlayer(1);
        CardGame cardGame = SolShowCardGameHelper.createFixedSolShowCardGame(player1, player2);
        CommandHistory cmdHistory = new CommandHistory(false, true);
        
        IContextService ctxtServ = Services.get(IContextService.class);
        Context context = ctxtServ.getThreadContext();
        assertNotNull(context);
        
        State state = context.limitedContext.getState();
        assertTrue(state.getPlayers().isEmpty());
        assertNull(state.getLocalId());
        assertNull(state.getCardGame());        
        assertNull(state.getServerId());
        assertTrue(state.getServers().isEmpty());
        assertNotNull(state.getCommandHistory());
        
        Server server = ServerHelper.createFixedServer();
        
        state.getServers().add(server);
        state.setServerId(server.id);
        state.getPlayers().add(player1);
        state.getPlayers().add(player2);
        state.setCardGame(cardGame);

        // initial CommandHistory state
        assertEquals(-1, cmdHistory.getAcceptedIndex());
        assertEquals(-1, cmdHistory.getCurrentIndex());
        assertEquals(-1, cmdHistory.getLastIndex());
        assertEquals(0, cmdHistory.getSize());   
        
        // We'll use these stacks in the test
        CardStack cs_p1depot = cardGame.getCardStack(SolShowTestIds.P1_DEPOT);
        CardStack cs_p1turnover = cardGame.getCardStack(SolShowTestIds.P1_TURNOVER);
        
        // add local command, awaiting server confirmation
        Card card1 = cs_p1depot.getHighestCard();
        C_SolShowMove cmd1 = new C_SolShowMove(cs_p1depot.id, cs_p1turnover.id, card1.id);
        cmd1.setSourceId(player1.id);
        cmdHistory.addAwaiting(cmd1, state);        
        // test state
        assertEquals(-1, cmdHistory.getAcceptedIndex());
        assertEquals(0, cmdHistory.getCurrentIndex());
        assertEquals(0, cmdHistory.getLastIndex());
        assertEquals(1, cmdHistory.getSize());        
        
        // server accepts cmd1
        cmdHistory.accept(cmd1.id);
        // test state
        assertEquals(0, cmdHistory.getAcceptedIndex());
        assertEquals(0, cmdHistory.getCurrentIndex());
        assertEquals(0, cmdHistory.getLastIndex());
        assertEquals(1, cmdHistory.getSize());
        
        // add another 2 local commands, both awaiting server confirmation
        Card card2 = cs_p1depot.getHighestCard();
        C_SolShowMove cmd2 = new C_SolShowMove(cs_p1depot.id, cs_p1turnover.id, card2.id);
        cmd2.setSourceId(player1.id);
        cmdHistory.addAwaiting(cmd2, state);
        // test state
        assertEquals(0, cmdHistory.getAcceptedIndex());
        assertEquals(1, cmdHistory.getCurrentIndex());
        assertEquals(1, cmdHistory.getLastIndex());
        assertEquals(2, cmdHistory.getSize());
        // third card
        Card card3 = cs_p1depot.getHighestCard();
        C_SolShowMove cmd3 = new C_SolShowMove(cs_p1depot.id, cs_p1turnover.id, card3.id);
        cmd3.setSourceId(player1.id);
        cmdHistory.addAwaiting(cmd3, state);    
        // test state
        assertEquals(0, cmdHistory.getAcceptedIndex());
        assertEquals(2, cmdHistory.getCurrentIndex());
        assertEquals(2, cmdHistory.getLastIndex());
        assertEquals(3, cmdHistory.getSize());
        
        // server accepts cmd2
        cmdHistory.accept(cmd2.id);
        // test state
        assertEquals(1, cmdHistory.getAcceptedIndex());
        assertEquals(2, cmdHistory.getCurrentIndex());
        assertEquals(2, cmdHistory.getLastIndex());
        assertEquals(3, cmdHistory.getSize());
        
        // server accepts cmd3
        cmdHistory.accept(cmd2.id);
        // test state
        assertEquals(2, cmdHistory.getAcceptedIndex());
        assertEquals(2, cmdHistory.getCurrentIndex());
        assertEquals(2, cmdHistory.getLastIndex());
        assertEquals(3, cmdHistory.getSize());
    }

    
}
