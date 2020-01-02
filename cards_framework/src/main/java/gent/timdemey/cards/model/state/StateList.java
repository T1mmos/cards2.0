package gent.timdemey.cards.model.state;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

class StateList<X> implements List<X>
{
	private final State state;
	private final List<X> list;

	private StateList(State state, List<X> wrappee)
	{
		this.state = state;
		this.list = wrappee;
	}

	static <X> StateList<X> create(State state, List<X> wrappee)
	{
		StateList<X> stateList = new StateList<>(state, wrappee);
		return stateList;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean addAll(Collection<? extends X> c)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends X> c)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean contains(Object o)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public X get(int index)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int indexOf(Object o)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<X> iterator()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int lastIndexOf(Object o)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ListIterator<X> listIterator()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<X> listIterator(int index)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Object o)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public X remove(int index)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public X set(int index, X element)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<X> subList(int fromIndex, int toIndex)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
