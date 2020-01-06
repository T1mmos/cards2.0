package gent.timdemey.cards.model.state;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

import gent.timdemey.cards.model.EntityBase;

public class StateListRef<X extends EntityBase> extends StateRef implements List<X>
{
	private final List<X> list;

	private StateListRef(State state, List<X> wrappee)
	{
		super(state);
		this.list = wrappee;
	}

	public static <X extends EntityBase> StateListRef<X> create(State state, List<X> wrappee)
	{
		StateListRef<X> stateList = new StateListRef<>(state, wrappee);
		return stateList;
	}

	public X get(UUID id)
	{
		for (X x : list)
		{
			if (x.id.equals(id))
			{
				return x;
			}
		}
		throw new IllegalArgumentException("No entity found in this list for id=" + id);
	}

	public List<UUID> getIds()
	{
		List<UUID> ids = new ArrayList<>();
		for (X x : list)
		{
			ids.add(x.id);
		}
		return ids;
	}

	public List<X> getExcept(UUID... excluded)
	{
		List<UUID> exclIds = Arrays.asList(excluded);
		List<X> xs = new ArrayList<>();
		for (X x : list)
		{
			if (!exclIds.contains(x.id))
			{
				xs.add(x);
			}
		}
		return xs;
	}

	public List<X> getExcept(StateValueRef<UUID>... excluded)
	{
		UUID[] exclIds = new UUID[excluded.length];
		for (int i = 0; i < excluded.length; i++)
		{
			StateValueRef<UUID> ref = excluded[i];
			UUID id = ref.get();
			exclIds[i] = id;
		}
		return getExcept(exclIds);
	}

	public boolean contains(UUID id)
	{
		return list.stream().anyMatch(x -> x.id.equals(id));
	}

	@Override
	public boolean add(X e)
	{
		boolean added = list.add(e);

		if (added)
		{
			state.getStateDelta().recordListAdd(this, e);
		}

		return added;
	}

	@Override
	public void add(int index, X element)
	{
		list.add(index, element);
		state.getStateDelta().recordListAdd(this, element);
	}

	@Override
	public boolean addAll(Collection<? extends X> c)
	{
		boolean added = list.addAll(c);
		if (added)
		{
			for (X x : c)
			{
				state.getStateDelta().recordListAdd(this, x);
			}
		}
		return added;
	}

	@Override
	public boolean addAll(int index, Collection<? extends X> c)
	{
		boolean added = list.addAll(index, c);
		if (added)
		{
			for (X x : c)
			{
				state.getStateDelta().recordListAdd(this, x);
			}
		}
		return added;
	}

	@Override
	public void clear()
	{
		List<X> copy = new ArrayList<>(list);
		list.clear();
		for (X x : copy)
		{
			state.getStateDelta().recordListRemove(this, x);
		}
	}

	@Override
	public boolean contains(Object o)
	{
		return list.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		return list.containsAll(c);
	}

	@Override
	public X get(int index)
	{
		return list.get(index);
	}

	@Override
	public int indexOf(Object o)
	{
		return list.indexOf(o);
	}

	@Override
	public boolean isEmpty()
	{
		return list.isEmpty();
	}

	@Override
	public Iterator<X> iterator()
	{
		return list.iterator();
	}

	@Override
	public int lastIndexOf(Object o)
	{
		return lastIndexOf(o);
	}

	@Override
	public ListIterator<X> listIterator()
	{
		return list.listIterator();
	}

	@Override
	public ListIterator<X> listIterator(int index)
	{
		return listIterator(index);
	}

	@Override
	public boolean remove(Object o)
	{
		boolean removed = list.remove(o);
		if (removed)
		{
			X x = (X) o;
			state.getStateDelta().recordListRemove(this, x);
		}
		return removed;
	}

	@Override
	public X remove(int index)
	{
		X x = list.remove(index);
		if (x != null)
		{
			state.getStateDelta().recordListRemove(this, x);
		}
		return x;
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		boolean removed = list.removeAll(c);
		if (removed)
		{
			for (Object o : c)
			{
				X x = (X) o;
				state.getStateDelta().recordListRemove(this, x);
			}
		}
		return removed;
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		boolean removed = false;
		removeIf(x -> {
			boolean remove = !c.contains(x);
			state.getStateDelta().recordListRemove(this, x);
			return remove;
		});

		return removed;
	}

	@Override
	public X set(int index, X element)
	{
		X xPrev = list.set(index, element);
		if (xPrev != null)
		{
			state.getStateDelta().recordListRemove(this, xPrev);
		}
		state.getStateDelta().recordListAdd(this, element);
		return xPrev;
	}

	@Override
	public int size()
	{
		return list.size();
	}

	@Override
	public List<X> subList(int fromIndex, int toIndex)
	{
		return list.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray()
	{
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		return list.toArray(a);
	}
}
