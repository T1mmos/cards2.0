package gent.timdemey.cards.services.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
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

public class CommandHistoryUnitTest
{
    @BeforeClass
    public static void init()
    {
        Services.install(ICardPlugin.class, new SolShowPlugin());
        
        MockContextService mockCtxtServ = new MockContextService();                
        mockCtxtServ.initialize(ContextType.UI);
        mockCtxtServ.initialize(ContextType.Client);        
        Services.install(IContextService.class, mockCtxtServ);
    }

    private IContextService ctxtServ;
    private Context context;
    private State state;
    private CommandHistory cmdHistory;
    private Player player1;
    private Player player2;
    
    // we'll always use these stacks in the tests
    private CardStack cs_p1depot;
    private CardStack cs_p1turnover;
    
    @Before
    public void resetGame()
    {
        ctxtServ = Services.get(IContextService.class);
        context = ctxtServ.getThreadContext();
        state = context.limitedContext.getState();
        cmdHistory = new CommandHistory(false, true);

        // reset players
        player1 = PlayerHelper.getFixedPlayer(0);
        player2 = PlayerHelper.getFixedPlayer(1);
        state.getPlayers().clear();
        state.getPlayers().add(player1);
        state.getPlayers().add(player2);
        state.setLocalId(player1.id);
        
        // reset card game
        CardGame cardGame = SolShowCardGameHelper.createFixedSolShowCardGame(player1, player2);
        state.setCardGame(cardGame);
        cs_p1depot = state.getCardGame().getCardStack(SolShowTestIds.P1_DEPOT);
        cs_p1turnover = state.getCardGame().getCardStack(SolShowTestIds.P1_TURNOVER);
        
        // reset server
        Server server = ServerHelper.createFixedServer();
        state.getServers().clear();
        state.getServers().add(server);
        state.setServerId(server.id);
        
        // reset command history
        state.setCommandHistory(cmdHistory);        
    }
    
    @Test 
    public void initialState()
    {
        assertEquals(-1, cmdHistory.getAcceptedIndex());
        assertEquals(-1, cmdHistory.getCurrentIndex());
        assertEquals(-1, cmdHistory.getLastIndex());
        assertEquals(0, cmdHistory.getSize());   
    }
    
    @Test
    public void awaitAcceptAwaitAwaitAcceptAccept()
    {        
        // add local command, awaiting server confirmation
        C_SolShowMove cmd1 = CreateNextMoveCommand();
        cmdHistory.addAwaiting(cmd1, state);        
        // test state
        assertEquals(-1, cmdHistory.getAcceptedIndex());
        assertEquals(0, cmdHistory.getCurrentIndex());
        assertEquals(0, cmdHistory.getLastIndex());
        assertEquals(1, cmdHistory.getSize());        
        
        // server accepts cmd1
        cmdHistory.accept(cmd1.id, state);
        // test state
        assertEquals(0, cmdHistory.getAcceptedIndex());
        assertEquals(0, cmdHistory.getCurrentIndex());
        assertEquals(0, cmdHistory.getLastIndex());
        assertEquals(1, cmdHistory.getSize());
        
        // add another 2 local commands, both awaiting server confirmation
        C_SolShowMove cmd2 = CreateNextMoveCommand();
        cmdHistory.addAwaiting(cmd2, state);
        // test state
        assertEquals(0, cmdHistory.getAcceptedIndex());
        assertEquals(1, cmdHistory.getCurrentIndex());
        assertEquals(1, cmdHistory.getLastIndex());
        assertEquals(2, cmdHistory.getSize());
        // third card
        C_SolShowMove cmd3 = CreateNextMoveCommand();
        cmdHistory.addAwaiting(cmd3, state);    
        // test state
        assertEquals(0, cmdHistory.getAcceptedIndex());
        assertEquals(2, cmdHistory.getCurrentIndex());
        assertEquals(2, cmdHistory.getLastIndex());
        assertEquals(3, cmdHistory.getSize());
        
        // server accepts cmd2
        cmdHistory.accept(cmd2.id, state);
        // test state
        assertEquals(1, cmdHistory.getAcceptedIndex());
        assertEquals(2, cmdHistory.getCurrentIndex());
        assertEquals(2, cmdHistory.getLastIndex());
        assertEquals(3, cmdHistory.getSize());
        
        // server accepts cmd3
        cmdHistory.accept(cmd2.id, state);
        // test state
        assertEquals(2, cmdHistory.getAcceptedIndex());
        assertEquals(2, cmdHistory.getCurrentIndex());
        assertEquals(2, cmdHistory.getLastIndex());
        assertEquals(3, cmdHistory.getSize());
    }

    @Test
    public void awaitReject()
    {
        // add local command, awaiting server confirmation
        C_SolShowMove cmd1 = CreateNextMoveCommand();
        cmdHistory.addAwaiting(cmd1, state);        
        // test state
        assertEquals(-1, cmdHistory.getAcceptedIndex());
        assertEquals(0, cmdHistory.getCurrentIndex());
        assertEquals(0, cmdHistory.getLastIndex());
        assertEquals(1, cmdHistory.getSize());        
        
        // server rejects cmd1
        cmdHistory.reject(cmd1.id, state);
        // test state
        assertEquals(-1, cmdHistory.getAcceptedIndex());
        assertEquals(-1, cmdHistory.getCurrentIndex());
        assertEquals(-1, cmdHistory.getLastIndex());
        assertEquals(0, cmdHistory.getSize());
    }
        
    @Test
    public void awaitAwaitRejectAccept()
    {
        // add local command, awaiting server confirmation
        C_SolShowMove cmd1 = CreateNextMoveCommand();
        cmdHistory.addAwaiting(cmd1, state);        
        // test state
        assertEquals(-1, cmdHistory.getAcceptedIndex());
        assertEquals(0, cmdHistory.getCurrentIndex());
        assertEquals(0, cmdHistory.getLastIndex());
        assertEquals(1, cmdHistory.getSize());        
        
        // add another command, awaiting server confirmation
        C_SolShowMove cmd2 = CreateNextMoveCommand();
        cmdHistory.addAwaiting(cmd2, state);        
        // test state
        assertEquals(-1, cmdHistory.getAcceptedIndex());
        assertEquals(1, cmdHistory.getCurrentIndex());
        assertEquals(1, cmdHistory.getLastIndex());
        assertEquals(2, cmdHistory.getSize());        
        
        // server rejects cmd1
        cmdHistory.reject(cmd1.id, state);
        // test state
        assertEquals(-1, cmdHistory.getAcceptedIndex());
        assertEquals(0, cmdHistory.getCurrentIndex());
        assertEquals(0, cmdHistory.getLastIndex());
        assertEquals(1, cmdHistory.getSize());
        
        // server accepts cmd2
        cmdHistory.accept(cmd2.id, state);
        // test state
        assertEquals(0, cmdHistory.getAcceptedIndex());
        assertEquals(0, cmdHistory.getCurrentIndex());
        assertEquals(0, cmdHistory.getLastIndex());
        assertEquals(1, cmdHistory.getSize());
    }
    
    @Test
    public void awaitInjectQInjectAccept()
    {
        // in this test, we'll need some C_Move i.o. C_SolShowMove commands 
        // to step out of the Solitaire Showdown business rules, to trigger
        // and test a quarantained -> accepted transition. With adherence to
        // Solitaire Showdown business rules, this transition cannot be
        // triggered.
        
        fail("Needs implementation");
    }
    
    @Test
    public void awaitInjectInjectAccept()
    {        
        fail("Needs implementation");
    }
    
    private C_SolShowMove CreateNextMoveCommand()
    {
        Card card = cs_p1depot.getHighestCard();
        C_SolShowMove cmd = new C_SolShowMove(cs_p1depot.id, cs_p1turnover.id, card.id);
        cmd.setSourceId(player1.id);
        return cmd;
    }
}
