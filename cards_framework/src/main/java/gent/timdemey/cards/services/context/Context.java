package gent.timdemey.cards.services.context;

import java.util.ArrayList;
import java.util.List;

import gent.timdemey.cards.readonlymodel.IGameEventListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyEntityFactory;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.ICommandExecutionService;
import gent.timdemey.cards.services.IContextListener;

public final class Context implements IContextBase
{
    final LimitedContext limitedContext;
    
    private final List<IGameEventListener> gameEventListeners;        
    private final List<IContextListener> contextListeners;
    
    Context(ContextType contextType, ICommandExecutionService cmdExecService) 
    {        
        limitedContext = new LimitedContext(contextType, cmdExecService);
                
        this.contextListeners = new ArrayList<>();
        this.gameEventListeners = new ArrayList<>();
    }
    
    public void addContextListener(IContextListener contextListener)
    {
        this.contextListeners.add(contextListener);
    }
    
    public void removeContextListener(IContextListener contextListener)
    {
        this.contextListeners.remove(contextListener);
    }
    
    public ReadOnlyState getReadOnlyState() 
    {
       return ReadOnlyEntityFactory.getOrCreateState(limitedContext.getState());
    }

    @Override
    public ContextType getContextType()
    {
        return limitedContext.getContextType();
    }
}
