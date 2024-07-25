package gent.timdemey.cards.model.delta;

import java.util.UUID;

import gent.timdemey.cards.utils.Debug;

public abstract class StateRef<X>
{
    protected final IChangeTracker _ChangeTracker;
            
    public final Property<X> property;
    public final UUID entityId;

    protected StateRef(IChangeTracker changeTracker, Property<X> property, UUID entityId)
    {
        this._ChangeTracker = changeTracker;
        
        this.property = property;
        this.entityId = entityId;
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
