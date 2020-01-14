package gent.timdemey.cards.services.context;

import java.util.ArrayList;
import java.util.List;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.commands.CommandBase;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyEntityFactory;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.ICommandExecutionService;
import gent.timdemey.cards.services.IContextService;

public final class Context
{
    // calculated
    private final IChangeTracker changeTracker;
    
    final LimitedContext limitedContext;
    
    private final List<IStateListener> stateListeners;  
    
    Context(ContextType contextType, ICommandExecutionService cmdExecService, boolean trackChanges) 
    {        
        limitedContext = new LimitedContext(contextType, cmdExecService);
                
        if (trackChanges)
        {
            limitedContext.setExecutionListener(this::onExecuted);
        }
        
        this.stateListeners = new ArrayList<>();
        this.changeTracker = trackChanges ? new StateChangeTracker() : new NopChangeTracker();
    }
    
    public ReadOnlyState getReadOnlyState() 
    {
       return ReadOnlyEntityFactory.getOrCreateState(limitedContext.getState());
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
        if (!isCurrentContext)
        {
            throw new IllegalStateException("You can only schedule on the Context on the correct thread, expected context type = " + getContextType());
        }
        
        limitedContext.schedule(command);
    }
    
    public void addStateListener(IStateListener stateListener)
    {
        stateListeners.add(stateListener);
    }
    
    // Callback from execution service that lets us know that a command or a chain of commands was executed.
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
