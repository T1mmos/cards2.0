package gent.timdemey.cards.services.context;

import java.util.UUID;

import gent.timdemey.cards.model.state.Property;
import gent.timdemey.cards.model.state.StateListRef;
import gent.timdemey.cards.model.state.StateRef;
import gent.timdemey.cards.model.state.StateValueRef;

public class Change<X>
{
	public final ChangeType changeType;
	public final Property property;
	public final UUID entityId;
	final StateRef stateRef;
	final X oldValue;
	final X newValue;
	final X addedValue;
	final X removedValue;
	
	private Change(ChangeType changeType, StateRef stateRef, Property property, UUID entityId, X oldValue, X newValue)
	{
		this.changeType = changeType;
		this.stateRef = stateRef;
		this.property = property;
		this.entityId = entityId;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.addedValue = null;
		this.removedValue = null;
	}
		
	static <X> Change forSet(StateValueRef<X> stateRef, X oldValue, X newValue)
	{
		Change<X> change = new Change<>(
				ChangeType.Set,
				stateRef,
				stateRef.property,
				stateRef.entityId,
				oldValue, 
				newValue);
		return change;
	}
	
	static <X> Change<X> forAdd(StateListRef<X> stateList, X added)
	{
		Change<X> change = new Change<>(
				ChangeType.Add,
				stateList,
				stateList.property,
				stateList.entityId,
				added, 
				null);
		return change;
	}
	

	static <X> Change<X> forRemove(StateListRef<X> stateList, X removed)
	{
		Change<X> change = new Change<>(
				ChangeType.Remove,
				stateList,
				stateList.property,
				stateList.entityId,
				null, 
				removed);
		return change;
	}
}
