package gent.timdemey.cards.services.context;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.state.State;

public final class LimitedContext
{
    private final ContextType contextType;
    private final ICommandExecutor cmdExecServ;
    private final State state;

    public LimitedContext(ContextType contextType, ICommandExecutor cmdExecServ)
    {
        Preconditions.checkNotNull(contextType);
        Preconditions.checkNotNull(cmdExecServ);

        this.contextType = contextType;
        this.cmdExecServ = cmdExecServ;
        this.state = new State();
    }

    public ContextType getContextType()
    {
        return contextType;
    }

    public void schedule(CommandBase command)
    {
        if (command.getSourceId() == null)
        {
            command.setSourceId(state.getLocalId());
        }
        cmdExecServ.schedule(command, state);
    }

    void addExecutionListener(IExecutionListener executionListener)
    {
        cmdExecServ.addExecutionListener(executionListener);
    }
    
    void removeExecutionListener(IExecutionListener executionListener)
    {
        cmdExecServ.removeExecutionListener(executionListener);
    }

    State getState()
    {
        return state;
    }

    public CommandHistory getCommandHistory()
    {
        return cmdExecServ.getCommandHistory();
    }
}
