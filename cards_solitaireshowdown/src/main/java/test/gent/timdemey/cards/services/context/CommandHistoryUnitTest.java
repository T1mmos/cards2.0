package gent.timdemey.cards.services.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
import gent.timdemey.cards.model.entities.cards.Suit;
import gent.timdemey.cards.model.entities.cards.Value;
import gent.timdemey.cards.model.entities.commands.C_Move;
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
    private CardGame cardGame;
    
    // we'll always one or more of these stacks in the tests
    private CardStack cs_p1depot;
    private CardStack cs_p1turnover;
    private CardStack cs_p1special;
    private CardStack cs_p2special;
    private CardStack cs_p1laydown1;
    
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
        cardGame = SolShowCardGameHelper.createFixedSolShowCardGame(player1, player2);
        state.setCardGame(cardGame);
        cs_p1depot = cardGame.getCardStack(SolShowTestIds.P1_DEPOT);
        cs_p1turnover = cardGame.getCardStack(SolShowTestIds.P1_TURNOVER);
        cs_p1special = cardGame.getCardStack(SolShowTestIds.P1_SPECIAL);
        cs_p2special = cardGame.getCardStack(SolShowTestIds.P2_SPECIAL);
        cs_p1laydown1 = cardGame.getCardStack(SolShowTestIds.P1_LAYDOWN1);
        
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
    
    /**
     * Tests putting a command in quarantine because of injecting a server command.
     */
    @Test
    public void awaitInjectQReject()
    {
        // in this test, we'll need some C_Move i.o. C_SolShowMove commands 
        // to step out of the Solitaire Showdown business rules, to trigger
        // and test a quarantined -> rejected transition. With adherence to
        // Solitaire Showdown business rules, this transition cannot be
        // triggered.
        
        // the first command needs to be a C_SolShowMove command because the business rules
        // determine if a command can be (re)executed. A C_Move command doesn't have any 
        // business rules defined. We'll take the SPECIAL stack of player 2 which has an ace
        // on top, so we can just create a C_SolShowMove without prior setup.
        
        // add local command, awaiting server confirmation
        Card c_p2 = cs_p2special.getHighestCard();
        assertEquals(Suit.CLUBS, c_p2.suit);
        assertEquals(Value.V_A, c_p2.value);
        C_SolShowMove cmd1 = new C_SolShowMove(cs_p2special.id, cs_p1laydown1.id, c_p2.id);
        cmd1.setSourceId(player2.id);
        cmdHistory.addAwaiting(cmd1, state);        
        // test state
        assertEquals(-1, cmdHistory.getAcceptedIndex());
        assertEquals(0, cmdHistory.getCurrentIndex());
        assertEquals(0, cmdHistory.getLastIndex());
        assertEquals(1, cmdHistory.getSize());        
        
        // inject another command. this one should be immediately accepted and injected
        // before the previous local command. Because the previous command will not be able
        // to execute (because the destination stack is not empty and the card to transfer
        // is an ace, and ofcourse business rules are in place as it is a C_SolShowMove), 
        // it must go in quarantine. we use a C_Move command here to neglect business rules.
        Card c_p1 = cs_p1special.getHighestCard();
        assertEquals(Suit.SPADES, c_p1.suit);
        assertEquals(Value.V_8, c_p1.value);
        C_Move cmd2 = new C_Move(cs_p1special.id, cs_p1laydown1.id, c_p1.id);
        cmd2.setSourceId(player1.id);
        cmdHistory.addAccepted(cmd2, state);        
        // test state
        assertEquals(0, cmdHistory.getAcceptedIndex());
        assertEquals(0, cmdHistory.getCurrentIndex());
        assertEquals(1, cmdHistory.getLastIndex());
        assertEquals(2, cmdHistory.getSize());

        CommandExecution cmdExec0 = cmdHistory.getCommandExecution(0);
        CommandExecution cmdExec1 = cmdHistory.getCommandExecution(1);
        
        assertNotNull(cmdExec0);
        assertNotNull(cmdExec1);
        
        // server injected the second command (C_Move)
        assertEquals(CommandExecutionState.Accepted, cmdExec0.cmdExecutionState.get());
        assertEquals(cmd2, cmdExec0.getCommand());
        
        // ... so the client must put the unexecutable next commands into Quarantine (C_SolShowMove)
        assertEquals(CommandExecutionState.Quarantined, cmdExec1.cmdExecutionState.get());
        assertEquals(cmd1, cmdExec1.getCommand());        
        
        // now the server rejects the quarantined command
        cmdHistory.reject(cmd1.id, state);
        // test state
        assertEquals(0, cmdHistory.getAcceptedIndex());
        assertEquals(0, cmdHistory.getCurrentIndex());
        assertEquals(0, cmdHistory.getLastIndex());
        assertEquals(1, cmdHistory.getSize());
        
    }
    
    /**
     * Tests putting a command in quarantine because of injecting two 
     * server commands. Special thing about this is that quarantined commands
     * are held at the end of the command chain, and thus the second injected
     * command must gracefully handle the dangling quarantined command. 
     */
    @Test
    public void awaitInjectQInjectReject()
    {
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
