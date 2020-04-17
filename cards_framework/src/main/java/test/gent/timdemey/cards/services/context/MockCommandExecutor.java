package gent.timdemey.cards.services.context;

import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.state.State;

public class MockCommandExecutor implements ICommandExecutor
{

    @Override
    public void schedule(CommandBase command, State state)
    {
        // MockCommandExecutor installed in Client layer, so
        // do nothing
    }

    @Override
    public void addExecutionListener(IExecutionListener executionListener)
    {
        // MockCommandExecutor installed in Client layer, no listeners
        // installed anyway
    }

    @Override
    public void removeExecutionListener(IExecutionListener executionListener)
    {
        // MockCommandExecutor installed in Client layer, no listeners
        // installed anyway
    }

    @Override
    public void shutdown()
    {
        // TODO Auto-generated method stub
        
    }

}
