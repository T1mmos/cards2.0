package gent.timdemey.cards.services.context;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.model.commands.CommandBase;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.ICommandExecutionService;

public final class LimitedContext
{
    private final ContextType contextType;
    private ICommandExecutionService cmdExecServ;    
    private final State state;
    
    public LimitedContext (ContextType contextType, ICommandExecutionService cmdExecServ)
    {
        Preconditions.checkNotNull(contextType);
        Preconditions.checkNotNull(cmdExecServ);
        
        this.contextType = contextType;
        this.cmdExecServ = cmdExecServ;
        this.state = State.create(contextType);
    }    
    
    public ContextType getContextType()
    {
        return contextType;
    }

	public void schedule(CommandBase command)
	{
		cmdExecServ.schedule(command, state);
	}

    State getState()
    {
        return state;
    }
}
