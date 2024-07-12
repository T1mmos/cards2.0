package gent.timdemey.cards.services.context;

import java.util.List;

import gent.timdemey.cards.model.state.StateListRef;
import gent.timdemey.cards.model.state.StateValueRef;

public interface IChangeTracker
{
    <X> void recordRefSet(StateValueRef<X> reference, X oldValue, X newValue);

    <X> void recordListAdd(StateListRef<X> ref, X e);

    <X> void recordListRemove(StateListRef<X> ref, X e);

    List<Change<?>> getChangeList();

    void reset();
}
