package gent.timdemey.cards.services.context;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<Change<?>> getChangeList()
    {
        return new ArrayList<>();
    }

    @Override
    public void reset()
    {
        // nothing to do
    }
}
