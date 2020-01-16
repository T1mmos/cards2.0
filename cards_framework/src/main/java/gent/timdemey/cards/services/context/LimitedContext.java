package gent.timdemey.cards.services.context;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.model.commands.CommandBase;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.ICommandExecutionService;
import gent.timdemey.cards.services.execution.IExecutionListener;

public final class LimitedContext
{
    private final ContextType contextType;
    private ICommandExecutionService cmdExecServ;
    private final State state;

    public LimitedContext(ContextType contextType, ICommandExecutionService cmdExecServ)
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
        cmdExecServ.schedule(command, state);
    }

    void setExecutionListener(IExecutionListener executionListener)
    {
        cmdExecServ.setExecutionListener(executionListener);
    }

    State getState()
    {
        return state;
    }
}
