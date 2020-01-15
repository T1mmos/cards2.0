package gent.timdemey.cards.services.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Objects;

import gent.timdemey.cards.model.state.StateListRef;
import gent.timdemey.cards.model.state.StateValueRef;

public class StateChangeTracker implements IChangeTracker
{
	private List<Change<?>> changes;
	
	StateChangeTracker()
	{
		this.changes = new ArrayList<Change<?>>();
	}
	
	private <X> Change<X> getChange(StateValueRef<X> stateRef)
	{
		// look for a previous change for the given reference
        for (int i = 0; i < changes.size(); i++)
        {
        	Change<?> chng = changes.get(i);
        	if (chng.stateRef != stateRef)
        	{
        		continue;
        	}
        	
        	return (Change<X>) chng;
        }
        
        return null;
	}
	
	private <X> Change<X> getChange(StateListRef<X> stateList, X element)
	{
		// look for a previous change for the given reference
        for (int i = 0; i < changes.size(); i++)
        {
        	Change<?> chng = changes.get(i);
        	if (chng.stateRef != stateList)
        	{
        		continue;
        	}
        	
        	Change<X> chngX = (Change<X>) chng;
        	
        	// only need the changes to the list that relate to the given element
        	if (!chngX.addedValue.equals(element) && !chngX.removedValue.equals(element))
        	{
        	    continue;
        	}
        	
        	return chngX;
        }
        
        return null;
	}
	
	@Override
	public <X> void recordRefSet (StateValueRef<X> reference, X oldValue, X newValue)
    {
    	// look for a previous change for the given reference
        Change<X> prevChangeX = getChange(reference);        
        
    	if (prevChangeX != null && !prevChangeX.newValue.equals(oldValue) || 
    		prevChangeX == null && oldValue != null)
        {
        	throw new IllegalStateException("Delta can't add a new change: oldvalue != prevChange.newValue");
        }
        
        // initial value before any changes were made
        X startValue;
        if (prevChangeX != null)
        {
            startValue = prevChangeX.oldValue;
        }
        else
        {
        	startValue = oldValue;
        }
        
        if (Objects.equal(startValue, newValue))
        {
        	// old change + new change leads to no change overall, so we can clear the previous and
        	// not record the current change
        	if (prevChangeX != null)
        	{
            	changes.remove(prevChangeX);
        	}
        }
        else
        {
        	Change<X> change = Change.forSet(reference, startValue, newValue);
        	
        	// replace the previous change and add the new change
        	changes.remove(prevChangeX);
        	changes.add(change);
        }
        
    }

	@Override
	public <X> void recordListAdd(StateListRef<X> ref, X e)
	{
		Change<X> prevChange = getChange(ref, e);
				
		if (prevChange != null)
		{
		    // if the same element is already in the list, then no change can't be recorded
	        if (prevChange.changeType == ChangeType.Add)
	        {
	            throw new IllegalStateException("Can't record the same element in a list twice for operation Add: " + ref + " -> " + e);
	        }
	        
	        if (prevChange.changeType != ChangeType.Remove)
	        {
	            throw new IllegalStateException("Unsupported previous change type for list Add: " + ref + " -> " + e);
	        }
	        
	        // remove the Remove record, as Remove + Add leads to no-op
	        changes.remove(prevChange);
		}
		else
		{
		    // add the Add record
		    Change<X> change = Change.forAdd(ref, e);
		    changes.add(change);
		}
	}

	@Override
	public <X> void recordListRemove(StateListRef<X> ref, X e)
    {
        Change<X> prevChange = getChange(ref, e);
        
        if (prevChange != null)
        {
            // if the same element is already in the list, then no change can't be recorded
            if (prevChange.changeType == ChangeType.Remove)
            {
                throw new IllegalStateException("Can't record the same element in a list twice for operation Remove: " + ref + " -> " + e);
            }
            
            if (prevChange.changeType != ChangeType.Add)
            {
                throw new IllegalStateException("Unsupported previous change type for list Remove: " + ref + " -> " + e);
            }
            
            // remove the Add record, as Add + Remove leads to no-op
            changes.remove(prevChange);
        }
        else
        {
            // add the Remove record
            Change<X> change = Change.forRemove(ref, e);
            changes.add(change);
        }
    }

    @Override
    public List<Change<?>> getChangeList()
    {
        return Collections.unmodifiableList(changes);
    }

    @Override
    public void reset()
    {
        changes.clear();
    }
}
