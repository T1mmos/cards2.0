package gent.timdemey.cards.model.state;

public final class StateValueRef<X> extends StateRef
{
	private X x;
    
    public StateValueRef()
    {
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
