package gent.timdemey.cards.model.state;

import java.util.List;

public abstract class StateRef
{
    protected final State state;
    
    protected StateRef(State state)
    {
        this.state = state;
    }
}
