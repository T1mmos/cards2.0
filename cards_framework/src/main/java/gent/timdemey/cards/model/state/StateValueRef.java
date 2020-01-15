package gent.timdemey.cards.model.state;

import java.util.UUID;

public final class StateValueRef<X> extends StateRef
{
	private X x;
    
    public StateValueRef(Property property, UUID entityId)
    {
        super(property, entityId);
        this.x = null;
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
