package gent.timdemey.cards.model.state;

import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.IChangeTracker;
import gent.timdemey.cards.utils.Debug;

public abstract class StateRef<X>
{
    public final Property<X> property;
    public final UUID entityId;

    protected StateRef(Property<X> property, UUID entityId)
    {
        this.property = property;
        this.entityId = entityId;
    }

    protected final IChangeTracker getChangeTracker()
    {
        IContextService contextService = Services.get(IContextService.class);
        IChangeTracker changeTracker = contextService.getThreadContext().getChangeTracker();
        return changeTracker;
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
