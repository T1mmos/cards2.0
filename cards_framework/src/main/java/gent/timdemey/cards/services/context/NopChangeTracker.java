package gent.timdemey.cards.services.context;

import gent.timdemey.cards.model.state.StateListRef;
import gent.timdemey.cards.model.state.StateValueRef;

public class NopChangeTracker implements IChangeTracker
{

    @Override
    public <X> void recordRefSet(StateValueRef<X> reference, X oldValue, X newValue)
    {
        
    }

    @Override
    public <X> void recordListAdd(StateListRef<X> ref, X e)
    {
        
    }

    @Override
    public <X> void recordListRemove(StateListRef<X> ref, X e)
    {
        
    }

}
