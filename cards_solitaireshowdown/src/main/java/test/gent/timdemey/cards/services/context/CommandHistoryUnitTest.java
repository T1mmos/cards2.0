package gent.timdemey.cards.services.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.cards.CardStack;
import gent.timdemey.cards.model.entities.commands.C_Move;
import gent.timdemey.cards.model.entities.commands.C_SolShowMove;
import gent.timdemey.cards.model.entities.commands.CommandExecution;
import gent.timdemey.cards.model.entities.commands.CommandExecutionState;
import gent.timdemey.cards.model.entities.commands.CommandHistory;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.entities.game.UDPServer;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.test.common.SolShowTestBase;
import gent.timdemey.cards.test.helpers.PlayerHelper;
import gent.timdemey.cards.test.helpers.ServerHelper;
import gent.timdemey.cards.test.helpers.SolShowCardGameHelper;
import gent.timdemey.cards.test.helpers.SolShowTestIds;

public class CommandHistoryUnitTest extends SolShowTestBase
{
    @BeforeClass
    public static void InitCommandHistoryUnitTest()
    { 
        installSolShowCardPlugin();
        installMockContextService();
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
    private CardStack cs_p1special; // 8 of spades as highest card
    private CardStack cs_p2special; // ace of clubs as highest card
    private CardStack cs_p1laydown1;
    private CardStack cs_p2laydown1;
    
    @Before
    public void resetGame()
    {
        ctxtServ = Services.get(IContextService.class);
        context = ctxtServ.getThreadContext();
        state = context.limitedContext.getState();
        cmdHistory = new CommandHistory(true);

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
        cs_p2laydown1 = cardGame.getCardStack(SolShowTestIds.P2_LAYDOWN1);
        
        // reset server
        UDPServer udpServer = ServerHelper.createFixedUDPServer();
        state.getUDPServers().clear();
        state.getUDPServers().add(udpServer);
        state.setServer(udpServer.server);
        
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
        // add local command, awaiting server confirmation
        C_SolShowMove cmd1 = CreateConflictingCommandClient();
        cmdHistory.addAwaiting(cmd1, state);        
        // test state
        assertEquals(-1, cmdHistory.getAcceptedIndex());
        assertEquals(0, cmdHistory.getCurrentIndex());
        assertEquals(0, cmdHistory.getLastIndex());
        assertEquals(1, cmdHistory.getSize());        
     
        C_Move cmd2 = CreateConflictingCommandServer();
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

        CommandExecution cmdExec2 = cmdHistory.getCommandExecution(0);
        assertEquals(CommandExecutionState.Accepted, cmdExec2.cmdExecutionState.get());
        assertEquals(cmd2, cmdExec2.getCommand());    
    }
    
    /**
     * Tests putting a command in quarantine because of injecting two 
     * server commands. Special thing about this is that quarantined commands
     * are held at the end of the command chain, and thus the second injected
     * command must gracefully handle the dangling quarantined command. Moreover
     * we also check that the game state is correct (which cards are at which 
     * stacks), which confirms the correct unexection and reexecution of 
     * commands within the CommandHistory with respect to Injects and Rejects.
     */
    @Test
    public void awaitInjectQInjectReject()
    {
        assertTrue(cs_p1special.getCardAt(12).equalsNotation("8♠")); // coincidence (see below)
        assertTrue(cs_p1special.getCardAt(11).equalsNotation("2♦"));
        assertTrue(cs_p2special.getCardAt(12).equalsNotation("A♣")); 
        assertTrue(cs_p2special.getCardAt(11).equalsNotation("8♠")); // coincidence in the fixed test game, also 8♠
        assertTrue(cs_p1laydown1.getCards().isEmpty());
        
        // add local command, awaiting server confirmation
        C_SolShowMove cmd1 = CreateConflictingCommandClient();
        cmdHistory.addAwaiting(cmd1, state);     
        
        assertTrue(cs_p1special.getHighestCard().equalsNotation("8♠"));  // not yet modified
        assertTrue(cs_p2special.getHighestCard().equalsNotation("8♠"));  // A♣ was moved to p1laydown1
        assertTrue(cs_p1laydown1.getHighestCard().equalsNotation("A♣")); // this confirms it
     
        // now a conflicting command from the server comes in. cmd1 will thus
        // rollback, cmd2 gets executed, then it is detected that cmd1 cannot 
        // reexecute and the command will thus become quarantined.
        C_Move cmd2 = CreateConflictingCommandServer();
        cmdHistory.addAccepted(cmd2, state);        

        assertTrue(cs_p1special.getHighestCard().equalsNotation("2♦"));  // the server command got executed, 8♠ was moved 
        assertTrue(cs_p2special.getHighestCard().equalsNotation("A♣"));  // A♣ was moved back to original stack, because command rollback
        assertTrue(cs_p1laydown1.getHighestCard().equalsNotation("8♠")); // this confirms that 8♠ was moved
        
        assertEquals(cmdHistory.getCommandExecution(0).getCommand().id, cmd2.id);
        assertEquals(cmdHistory.getCommandExecution(0).getExecutionState(), CommandExecutionState.Accepted);   
        assertEquals(cmdHistory.getCommandExecution(1).getCommand().id, cmd1.id);
        assertEquals(cmdHistory.getCommandExecution(1).getExecutionState(), CommandExecutionState.Quarantined);     
        
        // then another command from the server arrives, without conflict.
        C_Move cmd3 = CreateCommandServer();
        cmdHistory.addAccepted(cmd3, state);
        
        assertTrue(cs_p1special.getHighestCard().equalsNotation("A♣"));  // 2 cards were already taken from this stack (again coincidence with A♣)
        assertTrue(cs_p2laydown1.getHighestCard().equalsNotation("2♦")); // 2♦ was moved from p1special to p2laydown1
        assertTrue(cs_p2special.getHighestCard().equalsNotation("A♣"));  // didn't change
        assertTrue(cs_p1laydown1.getHighestCard().equalsNotation("8♠")); // didn't change

        CommandExecution cmdExec0 = cmdHistory.getCommandExecution(0);
        CommandExecution cmdExec1 = cmdHistory.getCommandExecution(1);
        CommandExecution cmdExec2 = cmdHistory.getCommandExecution(2);
                
        assertEquals(cmd2.id, cmdExec0.getCommand().id);
        assertEquals(cmd3.id, cmdExec1.getCommand().id);
        assertEquals(cmd1.id, cmdExec2.getCommand().id);
        
        assertEquals(CommandExecutionState.Accepted, cmdExec0.getExecutionState());
        assertEquals(CommandExecutionState.Accepted, cmdExec1.getExecutionState());
        assertEquals(CommandExecutionState.Quarantined, cmdExec2.getExecutionState());
        
        assertEquals(1, cmdHistory.getAcceptedIndex());
        assertEquals(1, cmdHistory.getCurrentIndex());
        assertEquals(2, cmdHistory.getLastIndex());
        assertEquals(3, cmdHistory.getSize());    
        
        // now a C_Reject command arrives from the server. Our quarantined command will 
        // thus completely leave the command history, as it is now completely clear that also the
        // server wasn't able to execute it. A quarantined command is always unexecuted,
        // thus the game state shouldn't be modified. The quarantined command is just 
        // completely wiped from the history.
        cmdHistory.reject(cmd1.id, state);
        
        assertTrue(cs_p1special.getHighestCard().equalsNotation("A♣"));  // didn't change
        assertTrue(cs_p2laydown1.getHighestCard().equalsNotation("2♦")); // didn't change
        assertTrue(cs_p2special.getHighestCard().equalsNotation("A♣"));  // didn't change
        assertTrue(cs_p1laydown1.getHighestCard().equalsNotation("8♠")); // didn't change
                               
        assertEquals(cmd2.id, cmdExec0.getCommand().id);
        assertEquals(cmd3.id, cmdExec1.getCommand().id);
        
        assertEquals(CommandExecutionState.Accepted, cmdExec0.getExecutionState());
        assertEquals(CommandExecutionState.Accepted, cmdExec1.getExecutionState());
        
        assertEquals(1, cmdHistory.getAcceptedIndex());
        assertEquals(1, cmdHistory.getCurrentIndex());
        assertEquals(1, cmdHistory.getLastIndex()); // decremented 1
        assertEquals(2, cmdHistory.getSize());      // decremented 1
    }
    
    /**
     * Almost the same test as awaitInjectQInjectReject, but the second 
     * inject command will resolve the conflict with the quarantined command. 
     * The server sends an Accept command which can be handled client-side,
     * because the quarantined command became executable again.
     */
    @Test
    public void awaitInjectQInjectRAccept()
    {        
        assertTrue(cs_p1special.getCardAt(12).equalsNotation("8♠")); // coincidence (see below)
        assertTrue(cs_p1special.getCardAt(11).equalsNotation("2♦"));
        assertTrue(cs_p2special.getCardAt(12).equalsNotation("A♣")); 
        assertTrue(cs_p2special.getCardAt(11).equalsNotation("8♠")); // coincidence in the fixed test game, also 8♠
        assertTrue(cs_p1laydown1.getCards().isEmpty());
        
        // add local command, awaiting server confirmation
        C_SolShowMove cmd1 = CreateConflictingCommandClient();
        cmdHistory.addAwaiting(cmd1, state);     
             
        // now a conflicting command from the server comes in. cmd1 will thus
        // rollback, cmd2 gets executed, then it is detected that cmd1 cannot 
        // reexecute and the command will thus become quarantined.
        C_Move cmd2 = CreateConflictingCommandServer();
        cmdHistory.addAccepted(cmd2, state);     
        
        // then another command from the server arrives. It will resolve the
        // conflict; the quarantined command will become executable
        // because the resolve command is the inverse of the conflicting command,
        // the effect is that the game situation will become the initial situation
        // (quarantined commands are not retried).
        C_Move cmd3 = CreateResolvingCommandServer();
        cmdHistory.addAccepted(cmd3, state);
        
        assertTrue(cs_p1special.getHighestCard().equalsNotation("8♠"));  // the card is put back now
        assertTrue(cs_p2laydown1.cards.isEmpty());                       // 8♠ just got taken
        assertTrue(cs_p2special.getHighestCard().equalsNotation("A♣"));  // didn't change
        assertTrue(cs_p1laydown1.cards.isEmpty());                       // didn't change

        // now a C_Accept command arrives from the server. Our quarantined command will 
        // thus be reexecuted.
        cmdHistory.accept(cmd1.id, state);
        
        assertTrue(cs_p2special.getHighestCard().equalsNotation("8♠"));  // A♣ was taken
        assertTrue(cs_p1laydown1.getHighestCard().equalsNotation("A♣")); // A♣ was added
        
        assertEquals(2, cmdHistory.getAcceptedIndex());
        assertEquals(2, cmdHistory.getCurrentIndex());
        assertEquals(2, cmdHistory.getLastIndex());
        assertEquals(3, cmdHistory.getSize());
        
        CommandExecution cmdExec0 = cmdHistory.getCommandExecution(0);
        CommandExecution cmdExec1 = cmdHistory.getCommandExecution(1);
        CommandExecution cmdExec2 = cmdHistory.getCommandExecution(2);
        
        assertEquals(cmd2.id, cmdExec0.getCommand().id);
        assertEquals(cmd3.id, cmdExec1.getCommand().id);
        assertEquals(cmd1.id, cmdExec2.getCommand().id);
        
        assertEquals(CommandExecutionState.Accepted, cmdExec0.getExecutionState());
        assertEquals(CommandExecutionState.Accepted, cmdExec1.getExecutionState());
        assertEquals(CommandExecutionState.Accepted, cmdExec2.getExecutionState());
    }
        
    private C_SolShowMove CreateNextMoveCommand()
    {
        Card card = cs_p1depot.getHighestCard();
        C_SolShowMove cmd = new C_SolShowMove(cs_p1depot.id, cs_p1turnover.id, card.id);
        cmd.setSourceId(player1.id);
        return cmd;
    }
    
    /**
     * Creates a client command that will be put into quarantine when a conflicting server 
     * command is injected.
     * @return
     */
    private C_SolShowMove CreateConflictingCommandClient()
    { 
        Card c_p2 = cs_p2special.getHighestCard();
        C_SolShowMove cmd = new C_SolShowMove(cs_p2special.id, cs_p1laydown1.id, c_p2.id);
        cmd.setSourceId(player2.id);
        return cmd;
    }
    
    /**
     * Create a server command that will be injected. A conflicting client command will 
     * be put into quarantine.
     * @return
     */
    private C_Move CreateConflictingCommandServer()
    {
        Card c_p1 = cs_p1special.getHighestCard();
        C_Move cmd = new C_Move(cs_p1special.id, cs_p1laydown1.id, c_p1.id);
        cmd.setSourceId(player1.id);
        return cmd;
    }
    
    /**
     * Create a server command that will be injected, and that modifies the state of the card game in
     * such a manner that a quarantined client command will now become executable. The client 
     * command should not be executed automatically, but when the Accept from the server arrives.
     * <p>This command is infact the inverse of CreateConflictingCommandServer, it moves the
     * card back to its original stack.
     */
    private C_Move CreateResolvingCommandServer()
    {        
        Card c_p1 = cs_p1laydown1.getHighestCard();
        C_Move cmd = new C_Move(cs_p1laydown1.id, cs_p1special.id, c_p1.id);
        
        cmd.setSourceId(player1.id);
        return cmd;
    }
    
    /**
     * Create a server command that will be injected. A conflicting client command will 
     * be put into quarantine.
     * @return
     */
    private C_Move CreateCommandServer()
    {
        Card c_p1 = cs_p1special.getHighestCard();
        C_Move cmd = new C_Move(cs_p1special.id, cs_p2laydown1.id, c_p1.id);
        cmd.setSourceId(player1.id);
        return cmd;
    }
}
