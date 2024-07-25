package gent.timdemey.cards.services.context;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.model.delta.StateChangeTracker;
import gent.timdemey.cards.model.delta.Change;
import gent.timdemey.cards.model.delta.IChangeTracker;
import gent.timdemey.cards.model.delta.ChangeType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.delta.Property;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyEntityFactory;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.interfaces.IContextService;

public final class Context
{
    // calculated
    private final IChangeTracker changeTracker;
    private final List<IStateListener> stateListeners;
            
    LimitedContext limitedContext;
    private boolean allowListeners;
    
    private final IContextService _ContextService;
    private final Logger _Logger;
    private final ICardPlugin _CardPlugin;
    private final ICommandExecutor _CommandExecutor;
    private final State _State;
    
    public Context(
            ICardPlugin cardPlugin, 
            ICommandExecutor commandExecutor, 
            IContextService contextService,
            State state,
            Logger logger)
    {
        this._CardPlugin = cardPlugin;
        this._CommandExecutor = commandExecutor;
        this._ContextService = contextService;
        this._State = state;
        this._Logger = logger;
        
        this.stateListeners = allowListeners ? new ArrayList<>() : null;
        this.changeTracker = allowListeners ? new StateChangeTracker() : new NopChangeTracker();
    }
    
    public ReadOnlyState getReadOnlyState()
    {
        return ReadOnlyEntityFactory.getOrCreateState(limitedContext.getState());
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

    public CanExecuteResponse canExecute(CommandBase command)
    {
        return limitedContext.canExecute(command);
    }

    public void schedule(CommandBase command)
    {
        boolean isCurrentContext = _ContextService.isCurrentContext(getContextType());
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

    void initialize(boolean allowListeners, ContextType contextType) 
    {   
        this.allowListeners = allowListeners;
        this.limitedContext = new LimitedContext(_CardPlugin, _CommandExecutor, _State, contextType);
        
        if (allowListeners)
        {
            addExecutionListener(this::onExecuted);    
        }     
    }

    private static class EntityPropertyChange
    {
        private final Property<?> property;
        private final UUID entityId;
        private final ChangeType changeType;
        
        private EntityPropertyChange ( Property<?> property, UUID entityId, ChangeType changeType)
        {
            this.property = property;
            this.entityId = entityId;
            this.changeType = changeType;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((changeType == null) ? 0 : changeType.hashCode());
            result = prime * result + ((entityId == null) ? 0 : entityId.hashCode());
            result = prime * result + ((property == null) ? 0 : property.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            EntityPropertyChange other = (EntityPropertyChange) obj;
            if (changeType != other.changeType)
                return false;
            if (entityId == null)
            {
                if (other.entityId != null)
                    return false;
            }
            else if (!entityId.equals(other.entityId))
                return false;
            if (property == null)
            {
                if (other.property != null)
                    return false;
            }
            else if (!property.equals(other.property))
                return false;
            return true;
        }   
    }
    
    // Callback from execution service that lets us know that a command or a chain
    // of commands was executed.
    // We can therefore now update the state listeners.
    private void onExecuted(CommandBase command)
    {
        // get a list of all changes since the last reset()
        List<Change<?>> changes = changeTracker.getChangeList();
                
        // do not leak EntityBase objects, convert to readonly counterparts
        List<ReadOnlyChange> roChanges = new ArrayList<>();
        
        Map<EntityPropertyChange, List<Object>> stateRef2ListAddedValues = new HashMap<>();
        Map<EntityPropertyChange, List<Object>> stateRef2ListRemovedValues = new HashMap<>();
        for (Change<?> change : changes)
        {
            if (change.changeType == ChangeType.Add)
            {
                EntityPropertyChange epc = new EntityPropertyChange(change.property, change.entityId, change.changeType);
                List<Object> list = stateRef2ListAddedValues.get(epc);
                if (list == null)
                {
                    list = new ArrayList<Object>();
                    stateRef2ListAddedValues.put(epc, list);
                }
                
                list.add(change.addedValue);
            } 
            else if (change.changeType == ChangeType.Remove)
            {
                EntityPropertyChange epc = new EntityPropertyChange(change.property, change.entityId, change.changeType);
                List<Object> list = stateRef2ListRemovedValues.get(epc);
                if (list == null)
                {
                    list = new ArrayList<Object>();
                    stateRef2ListRemovedValues.put(epc, list);
                }
                
                list.add(change.removedValue);
            }
            else
            {
                ReadOnlyChange roChange = ReadOnlyEntityFactory.getReadOnlyChangeValue(change);
                roChanges.add(roChange);
            }            
        }
        
        for (EntityPropertyChange epc : stateRef2ListAddedValues.keySet())
        {
            List<Object> addedValues = stateRef2ListAddedValues.get(epc);
            List<Object> removedValues = stateRef2ListRemovedValues.get(epc);
            ReadOnlyChange roChange = ReadOnlyEntityFactory.getReadOnlyChangeListValue(epc.changeType, epc.property, epc.entityId, addedValues, removedValues);
            roChanges.add(roChange);
        }
               
        // reset the tracker before updating all listeners,
        // this way listeners themselves may schedule commands and 
        // will not cause stackoverflows
        changeTracker.reset();
        
        if (roChanges.isEmpty())
        {
            _Logger.trace("No changes were detected in the Context. Not notifying any IStateListener instances.");
            return;
        }
        
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
