package gent.timdemey.cards.model.delta;

import java.util.List;

public interface IChangeTracker
{
    <X> void recordRefSet(StateValueRef<X> reference, X oldValue, X newValue);

    <X> void recordListAdd(StateListRef<X> ref, X e);

    <X> void recordListRemove(StateListRef<X> ref, X e);

    List<Change<?>> getChangeList();

    void reset();
}
