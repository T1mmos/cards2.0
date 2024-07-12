package gent.timdemey.cards.model.state;

import java.util.UUID;

import gent.timdemey.cards.utils.Debug;

public final class StateValueRef<X> extends StateRef<X>
{
    private X value;

    public StateValueRef(Property<X> property, UUID entityId)
    {
        this(property, entityId, null);
    }
    
    public StateValueRef(Property<X> property, UUID entityId, X initialValue)
    {
        super(property, entityId);
        this.value = initialValue;
    }

    public void set(X x)
    {
        // update the delta
        getChangeTracker().recordRefSet(this, this.value, x);

        this.value = x;
    }

    public X get()
    {
        return value;
    }
    
    @Override
    protected String toDebugString()
    {
        return Debug.getKeyValue("value", value);
    }
}
