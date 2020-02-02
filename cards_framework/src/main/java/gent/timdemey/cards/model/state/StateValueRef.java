package gent.timdemey.cards.model.state;

import java.util.UUID;

public final class StateValueRef<X> extends StateRef<X>
{
    private X x;

    public StateValueRef(Property<X> property, UUID entityId)
    {
        this(property, entityId, null);
    }
    
    public StateValueRef(Property<X> property, UUID entityId, X initialValue)
    {
        super(property, entityId);
        this.x = initialValue;
    }

    public void set(X x)
    {
        // update the delta
        getChangeTracker().recordRefSet(this, this.x, x);

        this.x = x;
    }

    public X get()
    {
        return x;
    }
}
