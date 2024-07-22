package gent.timdemey.cards.model.state;

import java.util.UUID;

import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.utils.Debug;

public abstract class StateRef<X>
{
    private final IChangeTracker _ChangeTracker;
            
    public final Property<X> property;
    public final UUID entityId;

    protected StateRef(Property<X> property, UUID entityId)
    {
        this.property = property;
        this.entityId = entityId;
        
        IContextService contextService = Services.get(IContextService.class);
        _ChangeTracker = contextService.getThreadContext().getChangeTracker();
    }

    protected final IChangeTracker getChangeTracker()
    {
        return _ChangeTracker;
    }
    
    @Override
    public final String toString()
    {
        return getClass().getSimpleName() +
            "\n{" + 
            Debug.getKeyValue("property", property.shortname) +   
            Debug.getKeyValue("entityId", entityId) + 
            toDebugString() +
            "\n}";
    }
    
    protected String toDebugString()
    {
        return "";
    }
}
