package gent.timdemey.cards.readonlymodel;

import java.util.UUID;

import gent.timdemey.cards.services.context.ChangeType;

public class ReadOnlyChange
{
    public final ChangeType changeType;
    public final ReadOnlyProperty<?> property;
    public final UUID entityId;
    public final Object oldValue;
    public final Object newValue;
    public final Object addedValue;
    public final Object removedValue;    
    
    ReadOnlyChange(ChangeType changeType, ReadOnlyProperty<?> property, UUID entityId, Object oldValue, Object newValue, Object addedValue, Object removedValue)
    {
        this.changeType = changeType;
        this.property = property;
        this.entityId = entityId;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.addedValue = addedValue;
        this.removedValue = removedValue;
    }
    
    @Override
    public String toString()
    {
        String str = String.format("ChangeType=%s,\nProperty=%s,\nOldValue=%s,\nNewValue=%s,\nAddedValue=%s,\nRemovedValue=%s",
            changeType, property, oldValue, newValue, addedValue, removedValue);
        return str;
    }
}
