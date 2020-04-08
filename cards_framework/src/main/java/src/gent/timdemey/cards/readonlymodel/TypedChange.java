package gent.timdemey.cards.readonlymodel;

import java.util.UUID;

import gent.timdemey.cards.services.context.ChangeType;

public class TypedChange<T>
{
    public final ChangeType changeType;
    public final ReadOnlyProperty<T> property;
    public final T oldValue;
    public final T newValue;
    public final T addedValue;
    public final T removedValue;   
    public final UUID entityId;
    
    TypedChange(ChangeType changeType, ReadOnlyProperty<T> property, UUID entityId, T oldValue, T newValue, T addedValue, T removedValue)
    {
        this.changeType = changeType;
        this.property = property;
        this.entityId = entityId;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.addedValue = addedValue;
        this.removedValue = removedValue;
    }
}
