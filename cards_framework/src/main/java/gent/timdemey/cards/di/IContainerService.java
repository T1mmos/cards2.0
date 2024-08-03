package gent.timdemey.cards.di;

import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.context.IContextListener;

/**
 * Provides container management.
 * 
 * @author Timmos
 */
public interface IContainerService
{
    /**
     * Returns the context based on the current thread. A thread has always full
     * access to its context, meaning that modifications to state are thread safe
     * because of thread-locality.
     * 
     * @return
     */
    public Container get(ContextType contextType);

    /**
     * Registers a context for the given context type (e.g. UI, server).
     * 
     * @param connection
     */
    public Container create(ContextType type);

    /**
     * Drops a context.
     */
    public void drop(ContextType type);

    /**
     * Checks if there is a context installed for the given context type.
     */
    public boolean isInitialized(ContextType type);
    
    public void addContextListener(IContextListener listener);
    
    public void removeContextListener(IContextListener listener);
}
