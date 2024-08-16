package gent.timdemey.cards.readonlymodel;

import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author Timmos
 */
public class ChangeList
{
    private final List<ReadOnlyChange> _Changes;
    
    public ChangeList(List<ReadOnlyChange> changes)
    {
        this._Changes = changes;
    }
    
    public List<ReadOnlyChange> GetChanges()
    {
        return new ArrayList<>(_Changes);
    }
    
    public boolean HasChange(ReadOnlyProperty<?> property)
    {
        List<ReadOnlyChange> changes = FindChanges(property);
        return !changes.isEmpty();
    }
    
    public boolean HasAnyChange(ReadOnlyProperty<?> ... properties)
    {
         for (ReadOnlyChange change : _Changes)
        {
            for (ReadOnlyProperty<?> prop : properties)
            {
                if (change.property == prop)
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    private List<ReadOnlyChange> FindChanges(ReadOnlyProperty<?> property)
    {
        List<ReadOnlyChange> changes = new ArrayList<>();
        for (ReadOnlyChange change : _Changes)
        {
            if (change.property == property)
            {
                changes.add(change);
            }
        }
        
        return changes;
    }
    
    public void OnChange(ReadOnlyProperty<?> property, Consumer<ReadOnlyChange> action)
    {
        List<ReadOnlyChange> changes = FindChanges(property);
        for (ReadOnlyChange change : changes)
        {
            action.accept(change);
        }
    }
    
    public void OnChange(ReadOnlyProperty<?> property, Runnable action)
    {
        List<ReadOnlyChange> changes = FindChanges(property);
        if (!changes.isEmpty())
        {
            action.run();
        }
    }    
}
