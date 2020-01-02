package gent.timdemey.cards.model.state;

public final class StateRef<X>
{
	private final State state;
	private X x;
    
    private StateRef(State state)
    {
    	this.state = state;
        this.x = null;
    }
    
    static <X> StateRef<X> create (State state)
    {
    	return new StateRef<X>(state);
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
