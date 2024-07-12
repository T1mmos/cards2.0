package gent.timdemey.cards.readonlymodel;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.services.context.ChangeType;

public class ReadOnlyChange
{
    public final ChangeType changeType;
    public final ReadOnlyProperty<?> property;
    public final UUID entityId;
    public final Object oldValue;
    public final Object newValue;
    public final List<Object> addedValues;
    public final List<Object> removedValues;    
    
    ReadOnlyChange(ChangeType changeType, ReadOnlyProperty<?> property, UUID entityId, Object oldValue, Object newValue, List<Object> addedValues, List<Object> removedValues)
    {
        this.changeType = changeType;
        this.property = property;
        this.entityId = entityId;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.addedValues = addedValues;
        this.removedValues = removedValues;
    }
    
    @Override
    public String toString()
    {
        String str = String.format("ChangeType=%s,\nProperty=%s,\nOldValue=%s,\nNewValue=%s,\nAddedValues=%s,\nRemovedValues=%s",
            changeType, property, oldValue, newValue, addedValues, removedValues);
        return str;
    }
}
