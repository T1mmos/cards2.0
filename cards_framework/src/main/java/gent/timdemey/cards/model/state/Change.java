package gent.timdemey.cards.model.state;

public class Change<X>
{
	final ChangeType changeType;
	final StateRef<X> stateRef;
	final StateList<X> stateList;
	final X oldValue;
	final X newValue;
	final X addedValue;
	final X removedValue;
	
	private Change(ChangeType changeType, StateRef<X> stateRef, X oldValue, X newValue)
	{
		this.changeType = changeType;
		this.stateRef = stateRef;
		this.stateList = null;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.addedValue = null;
		this.removedValue = null;
	}
	
	private Change(ChangeType changeType, StateList<X> stateList, X addedValue, X removedValue)
	{
		this.changeType = changeType;
		this.stateRef = null;
		this.stateList = stateList;
		this.oldValue = null;
		this.newValue = null;
		this.addedValue = addedValue;
		this.removedValue = removedValue;
	}
	
	static <X> Change<X> forSet(StateRef<X> stateRef, X oldValue, X newValue)
	{
		Change<X> change = new Change<>(
				ChangeType.Set,
				stateRef,
				oldValue, 
				newValue);
		return change;
	}
	
	static <X> Change<X> forAdd(StateList<X> stateList, X added)
	{
		Change<X> change = new Change<>(
				ChangeType.Add,
				stateList,
				added, 
				null);
		return change;
	}
	

	static <X> Change<X> forRemove(StateList<X> stateList, X removed)
	{
		Change<X> change = new Change<>(
				ChangeType.Remove,
				stateList,
				null, 
				removed);
		return change;
	}
}
