package gent.timdemey.cards.readonlymodel;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.services.context.ChangeType;

public class TypedChange<T>
{
    public final ChangeType changeType;
    public final ReadOnlyProperty<T> property;
    public final T oldValue;
    public final T newValue;
    public final List<T> addedValues;
    public final List<T> removedValues;   
    public final UUID entityId;
    
    TypedChange(ChangeType changeType, ReadOnlyProperty<T> property, UUID entityId, T oldValue, T newValue, List<T> addedValues, List<T> removedValues)
    {
        this.changeType = changeType;
        this.property = property;
        this.entityId = entityId;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.addedValues = addedValues;
        this.removedValues = removedValues;
    }
}
