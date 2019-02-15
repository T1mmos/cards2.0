    package gent.timdemey.cards.entities;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.SwingUtilities;

import com.google.common.base.Preconditions;

/**
 * The thread-local context provider.
 * @author Timmos
 *
 */
public final class ContextProvider implements IContextProvider {
        
    private final ConcurrentMap<ContextType, ContextFull> fullContexts;
        
    private List<IContextProviderListener> contextListeners;
    
    public ContextProvider() 
    {
        fullContexts = new ConcurrentHashMap<>();
        contextListeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public ContextFull getThreadContext() {
        ContextType type;
        
        if (SwingUtilities.isEventDispatchThread())
        {
            type = ContextType.UI;            
        }
        else if (Thread.currentThread().getName().equals(CommandProcessorClient.THREAD_NAME))
        {
            type = ContextType.Client;
        }
        else if (Thread.currentThread().getName().equals(CommandProcessorServer.THREAD_NAME))
        {
            type = ContextType.Server;
        }
        else
        {
            throw new IllegalThreadStateException("This thread has no access to context: " + Thread.currentThread().getName());
        }
        
        return getFullContext(type);
    }


    @Override
    public ContextLimited getContext(ContextType type) {
        ContextFull fullContext = fullContexts.get(type);        
        if (fullContext == null)
        {
            throw new IllegalStateException("No context installed for context type: " + type);
        }
        
        return new ContextLimited(fullContext);
    }

    @Override
    public void addContextListener(IContextProviderListener listener) {
        contextListeners.add(listener);
    }

    @Override
    public void removeContextListener(IContextProviderListener listener) {
        contextListeners.remove(listener);
    }

    private ContextFull getFullContext (ContextType contextType)
    {
        ContextFull context = fullContexts.get(contextType);
        if (context == null)
        {
            throw new IllegalStateException("No context installed for context type: " + contextType);
        }
        return context;
    }

    @Override
    public void installContext(ContextType type, UUID id, String name) {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());
                
        ICommandProcessor processor;
        switch (type)
        {
        case UI:
            processor = new CommandProcessorUI();
            break;
        case Client:
            processor = new CommandProcessorClient();
            break;
        case Server:
            processor = new CommandProcessorServer();
           // processor.process(new CommandCreateGame((CreateServerInfo) info));
            break;
        default:
            throw new IllegalArgumentException("Unknown type: " + type);
        }
        ContextFull prev = fullContexts.putIfAbsent(type, new ContextFull(type, processor, id, name));
        if (prev != null)
        {
            throw new ConcurrentModificationException("Context concurrently installed by different thread for type " + type);
        }
        
        for (IContextProviderListener listener : contextListeners)
        {
            listener.onContextAdded(type);
        }        
    }


    @Override
    public void dropContext(ContextType type) {
        ContextFull curr = fullContexts.remove(type);
        if (curr == null)
        {
            throw new IllegalStateException("Async processor currently unavailable, so cannot drop processor for type " + type);
        }
           
        for (IContextProviderListener listener : contextListeners)
        {
            listener.onContextRemoved(type);
        }
    }


    @Override
    public boolean isContextSet(ContextType type) {
        return fullContexts.containsKey(type);        
    }
}
