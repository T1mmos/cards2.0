package gent.timdemey.cards.model.delta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

public class StateListRef<X> extends StateRef<X> implements List<X>
{
    protected final List<X> list;

    public StateListRef(IChangeTracker changeTracker, Property<X> property, UUID entityId, List<X> wrappee)
    {
        super(changeTracker, property, entityId);
        this.list = wrappee;
    }

    @Override
    public boolean add(X e)
    {
        checkNotContains(e);

        boolean added = list.add(e);

        if(added)
        {
            getChangeTracker().recordListAdd(this, e);
        }

        return added;
    }

    @Override
    public void add(int index, X e)
    {
        checkNotContains(e);
        list.add(index, e);
        getChangeTracker().recordListAdd(this, e);
    }

    @Override
    public boolean addAll(Collection<? extends X> c)
    {
        checkNotContains(c);        
        
        boolean added = list.addAll(c);
        if(added)
        {
            for (X x : c)
            {
                getChangeTracker().recordListAdd(this, x);
            }
        }
        return added;
    }

    @Override
    public boolean addAll(int index, Collection<? extends X> c)
    {
        checkNotContains(c); 
        
        boolean added = list.addAll(index, c);
        if(added)
        {
            for (X x : c)
            {
                getChangeTracker().recordListAdd(this, x);
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
            getChangeTracker().recordListRemove(this, x);
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
        if(removed)
        {
            @SuppressWarnings("unchecked")
            X x = (X) o;
            getChangeTracker().recordListRemove(this, x);
        }
        return removed;
    }

    @Override
    public X remove(int index)
    {
        X x = list.remove(index);
        if(x != null)
        {
            getChangeTracker().recordListRemove(this, x);
        }
        return x;
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        boolean removed = list.removeAll(c);
        if(removed)
        {
            for (Object o : c)
            {
                @SuppressWarnings("unchecked")
                X x = (X) o;
                getChangeTracker().recordListRemove(this, x);
            }
        }
        return removed;
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        boolean removed = false;
        removeIf(x ->
        {
            boolean remove = !c.contains(x);
            getChangeTracker().recordListRemove(this, x);
            return remove;
        });

        return removed;
    }

    @Override
    public X set(int index, X element)
    {
        checkNotContains(element);
        
        X xPrev = list.set(index, element);
        if(xPrev != null)
        {
            getChangeTracker().recordListRemove(this, xPrev);
        }
        getChangeTracker().recordListAdd(this, element);
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
    
    private void checkNotContains(X e)
    {
        if(list.contains(e))
        {
            throw new IllegalStateException("A state list cannot contain equal elements: " + e);
        }
    }
    
    private void checkNotContains(Collection<? extends X> elements)
    {
        for (X x : elements)
        {
            checkNotContains(x);
        }
    }
}
