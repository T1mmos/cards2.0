package gent.timdemey.cards.di;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.context.IContextListener;

public class ContainerService implements IContainerService
{
    protected final ConcurrentMap<ContextType, Container> _Containers;
    private final Set<IContextListener> contextListeners;

    public ContainerService()
    {
        _Containers = new ConcurrentHashMap<>();
        contextListeners = Collections.synchronizedSet(new HashSet<>());
    }

    @Override
    public Container get(ContextType type)
    {
        Container container = _Containers.get(type);
        if (container == null)
        {
            throw new IllegalStateException("Container '" + type + "' unavailable, cannot get it");
        }
        
        return container;
    }

    @Override
    public Container create(ContextType contextType)
    {
        Container container = new Container();
        container.AddSingleton(IContainerService.class, this);
        _Containers.put(contextType, container);
        
        return container;
    }

    @Override
    public void drop(ContextType type)
    {
        Container container = _Containers.remove(type);
        if (container == null)
        {
            throw new IllegalStateException("Container '" + type + "' unavailable, cannot drop it");
        }

        synchronized (contextListeners)
        {
            for (IContextListener ctxtListener : contextListeners)
            {
                ctxtListener.onContextDropped(type);
            }
        }
    }

    @Override
    public boolean isInitialized(ContextType type)
    {
        return _Containers.containsKey(type);
    }

    @Override
    public void addContextListener(IContextListener listener)
    {
        contextListeners.add(listener);
    }

    @Override
    public void removeContextListener(IContextListener listener)
    {
        contextListeners.remove(listener);
    }
}