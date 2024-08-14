package gent.timdemey.cards.readonlymodel;

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
        ReadOnlyChange change = FindChange(property);
        return change != null;
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
    
    private ReadOnlyChange FindChange(ReadOnlyProperty<?> property)
    {
        for (ReadOnlyChange change : _Changes)
        {
            if (change.property == property)
            {
                return change;
            }
        }
        
        return null;
    }
    
    public void OnChange(ReadOnlyProperty<?> property, Consumer<ReadOnlyChange> action)
    {
        ReadOnlyChange change = FindChange(property);
        if (change != null)
        {
            action.accept(change);
        }
    }
    
    public void OnChange(ReadOnlyProperty<?> property, Runnable action)
    {
        ReadOnlyChange change = FindChange(property);
        if (change != null)
        {
            action.run();
        }
    }    
}
