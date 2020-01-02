package gent.timdemey.cards.model.state;

public final class StateValueRef<X> extends StateRef
{
	private X x;
    
    private StateValueRef(State state)
    {
    	super(state);
        this.x = null;
    }
    
    static <X> StateValueRef<X> create (State state)
    {
    	return new StateValueRef<X>(state);
    }
    
    void set(X x)
    {
    	// update the delta
    	state.getStateDelta().recordRefSet(this, this.x, x);
		
        this.x = x;
    }
    
    X get()
    {
        return x;
    }
}
