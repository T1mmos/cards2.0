package gent.timdemey.cards.services.context;

import java.util.ArrayList;
import java.util.List;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyEntityFactory;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.IContextService;

public final class Context
{
    // calculated
    private final IChangeTracker changeTracker;
    final LimitedContext limitedContext;
    private final List<IStateListener> stateListeners;
    private final boolean allowListeners;

    private Context(ContextType contextType, ICommandExecutor cmdExecService, boolean allowListeners)
    {
        limitedContext = new LimitedContext(contextType, cmdExecService);
        
        this.allowListeners = allowListeners;
        this.stateListeners = allowListeners ? new ArrayList<>() : null;
        this.changeTracker = allowListeners ? new StateChangeTracker() : new NopChangeTracker();
    }
    
    static Context createContext(ContextType contextType, ICommandExecutor cmdExecService, boolean allowListeners)
    {
        Context context = new Context(contextType, cmdExecService, allowListeners);
        
        if (allowListeners)
        {
            context.addExecutionListener(context::onExecuted);
        }
        
        return context;
    }

    public ReadOnlyState getReadOnlyState()
    {
        return ReadOnlyEntityFactory.getOrCreateState(limitedContext.getState());
    }

    void addExecutionListener(IExecutionListener executionListener)
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
        if (!allowListeners)
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
        
        // do not leak EntityBase objects, convert to readonly counterparts
        List<ReadOnlyChange> roChanges = new ArrayList<>();
        for (Change<?> change : changes)
        {
            ReadOnlyChange roChange = ReadOnlyEntityFactory.getReadOnlyChange(change);
            if (roChange != null)
            {
                roChanges.add(roChange);
            }
        }

        // reset the tracker before updating all listeners,
        // this way listeners themselves may schedule commands and 
        // will not cause stackoverflows
        changeTracker.reset();
        
        // as some listeners may unregister themselves during updates, guard against
        // concurrency exceptions
        List<IStateListener> listeners = new ArrayList<>(stateListeners);
        for (IStateListener sl : listeners)
        {
            for (ReadOnlyChange roChange : roChanges)
            {
                sl.onChange(roChange);
            }
        }


    }
}
