package gent.timdemey.cards.model.state;

public abstract class StateRef
{
    protected final State state;
    
    protected StateRef(State state)
    {
        this.state = state;
    }
}
