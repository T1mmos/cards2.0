package test.gent.timdemey.cards.services.context;

import static org.junit.Assert.fail;

import org.junit.Test;

import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.CommandExecution;
import gent.timdemey.cards.services.context.CommandHistory;

public class TestCommandHistory
{

    @Test
    public void test()
    {
        CommandExecution commandExecution = new CommandExecution(null, null);
        State state = new State();
        
        CommandHistory cmdHistory = new CommandHistory(false, true);
        cmdHistory.add(commandExecution, state);
        
        fail("Not yet implemented");
    }

}
