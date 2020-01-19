package gent.timdemey.cards.services.context;

import java.util.ArrayList;
import java.util.List;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.commands.CommandBase;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyEntityFactory;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.IContextService;

public final class Context
{
    // calculated
    private final IChangeTracker changeTracker;
    final LimitedContext limitedContext;
    private final List<IStateListener> stateListeners;

    Context(ContextType contextType, ICommandExecutor cmdExecService, boolean allowStateListeners)
    {
        limitedContext = new LimitedContext(contextType, cmdExecService);

        if(allowStateListeners)
        {
            limitedContext.addExecutionListener(this::onExecuted);
        }

        this.stateListeners = new ArrayList<>();
        this.changeTracker = allowStateListeners ? new StateChangeTracker() : new NopChangeTracker();
    }

    public ReadOnlyState getReadOnlyState()
    {
        return ReadOnlyEntityFactory.getOrCreateState(limitedContext.getState());
    }

    public CommandHistory getCommandHistory()
    {
        return limitedContext.getCommandHistory();
    }

    public void addExecutionListener(IExecutionListener executionListener)
    {
        limitedContext.addExecutionListener(executionListener);
    }

    public void removeExecutionListener(IExecutionListener executionListener)
    {
        limitedContext.removeExecutionListener(executionListener);
    }
    
    public ContextType getContextType()
    {
        return limitedContext.getContextType();
    }

    public IChangeTracker getChangeTracker()
    {
        return changeTracker;
    }

    public boolean canExecute(CommandBase command)
    {
        return command.canExecute(limitedContext.getState());
    }

    public void schedule(CommandBase command)
    {
        IContextService contextServ = Services.get(IContextService.class);
        boolean isCurrentContext = contextServ.isCurrentContext(getContextType());
        if(!isCurrentContext)
        {
            throw new IllegalStateException("You can only schedule on the Context on the correct thread, expected context type = " + getContextType());
        }

        limitedContext.schedule(command);
    }

    public void addStateListener(IStateListener stateListener)
    {
        if (changeTracker == null)
        {
            throw new IllegalStateException("This context isn't configured to track changes, therefore you cannot add a state listener.");
        }
        if (stateListeners.contains(stateListener))
        {
            throw new IllegalStateException("This state listener is already added.");
        }
        
        stateListeners.add(stateListener);
    }

    public void removeStateListener(IStateListener stateListener)
    {
        if (changeTracker == null)
        {
            throw new IllegalStateException("This context isn't configured to track changes, therefore you cannot remove a state listener.");
        }
        if (!stateListeners.contains(stateListener))
        {
            throw new IllegalStateException("This state listener cannot be found");
        }
        
        stateListeners.remove(stateListener);
    }

    // Callback from execution service that lets us know that a command or a chain
    // of commands was executed.
    // We can therefore now update the state listeners.
    private void onExecuted()
    {
        // get a list of all changes since the last reset()
        List<Change<?>> changes = changeTracker.getChangeList();

        for (IStateListener sl : stateListeners)
        {
            sl.onChange(changes);
        }

        // reset the tracker as we updated all listeners
        changeTracker.reset();
    }
}
