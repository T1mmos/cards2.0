package gent.timdemey.cards.services.context;

import static org.junit.Assert.fail;

import org.junit.Test;

import gent.timdemey.cards.model.state.State;

public class TestCommandHistory
{

    @Test
    public void test()
    {
        CommandExecution commandExecution = new CommandExecution(, null);
        State state = new State();
        
        CommandHistory cmdHistory = new CommandHistory(false, true);
        cmdHistory.add(commandExecution, state);
        
        fail("Not yet implemented");
    }

}
