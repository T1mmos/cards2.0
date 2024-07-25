package gent.timdemey.cards.model.delta;

import java.util.UUID;

import gent.timdemey.cards.model.delta.Property;
import gent.timdemey.cards.model.delta.StateListRef;
import gent.timdemey.cards.model.delta.StateRef;
import gent.timdemey.cards.model.delta.StateValueRef;

public class Change<X>
{
    public final ChangeType changeType;
    public final Property<X> property;
    public final UUID entityId;
    public final StateRef<X> stateRef;
    public final X oldValue;
    public final X newValue;
    public final X addedValue;
    public final X removedValue;

    private Change(ChangeType changeType, StateRef<X> stateRef, X oldValue, X newValue, X addedValue, X removedValue)
    {        
        this.changeType = changeType;
        this.stateRef = stateRef;
        this.property = stateRef.property;
        this.entityId = stateRef.entityId;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.addedValue = addedValue;
        this.removedValue = removedValue;
    }

    static <X> Change<X> forSet(StateValueRef<X> stateRef, X oldValue, X newValue)
    {
        Change<X> change = new Change<>(ChangeType.Set, stateRef, oldValue, newValue, null, null);
        return change;
    }

    static <X> Change<X> forAdd(StateListRef<X> stateList, X added)
    {
        Change<X> change = new Change<>(ChangeType.Add, stateList, null, null, added, null);
        return change;
    }

    static <X> Change<X> forRemove(StateListRef<X> stateList, X removed)
    {
        Change<X> change = new Change<>(ChangeType.Remove, stateList, null, null, null, removed);
        return change;
    }
}
