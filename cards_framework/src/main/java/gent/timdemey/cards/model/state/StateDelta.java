package gent.timdemey.cards.model.state;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Objects;

public class StateDelta
{
	private List<Change<?>> changes;
	
	StateDelta()
	{
		this.changes = new ArrayList<Change<?>>();
	}
	
	private <X> Change<X> getChange(StateRef<X> stateRef)
	{
		// look for a previous change for the given reference
        Change<X> prevChangeX = null;
        for (int i = 0; i < changes.size(); i++)
        {
        	Change<?> chng = changes.get(i);
        	if (chng.stateRef != stateRef)
        	{
        		continue;
        	}
        	
        	prevChangeX = (Change<X>) chng;
        	break;
        }
        return prevChangeX;
	}
	
	private <X> List<Change<X>> getChanges(StateList<X> stateList)
	{
		// look for a previous change for the given reference
		List<Change<X>> changesX = new ArrayList<Change<X>>();
        for (int i = 0; i < changes.size(); i++)
        {
        	Change<?> chng = changes.get(i);
        	if (chng.stateList != stateList)
        	{
        		continue;
        	}
        	
        	Change<X> chngX = (Change<X>) chng;
        	changesX.add(chngX);
        	break;
        }
        return changesX;
	}
	
    <X> void recordRefSet (StateRef<X> reference, X oldValue, X newValue)
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

	<X> void recordListAdd(StateList<X> stateList, X e)
	{
		getChanges(stateList);
	}

    <X> void recordListRemove(StateList<X> stateList, X e)
    {
    	
    }
}
