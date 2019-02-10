package gent.timdemey.cards.entities;

import java.util.UUID;

/**
 * Provides thread-based context e.g. data model and a command processor.
 * You can request limited context if you are not the owning thread of the
 * requested context; this gives you at least a command processor of the 
 * other context which you can use to push commands to other layers.
 * 
 * @author Timmos
 */
public interface IContextProvider {
            
    /**
     * Returns the context based on the current thread. A thread has always full
     * access to its context, meaning that modifications to state are thread
     * safe because of thread-locality.
     * @return
     */
    public ContextFull getThreadContext();
    
    /**
     * Returns the limited context based on the context type.
     * @param contextType
     * @return
     */
    public ContextLimited getContext (ContextType contextType); 
    
    /**
     * Listens to processor changes.
     * @param listener
     */
    public void addContextListener(IContextProviderListener listener);
    
    /**
     * Removes a processor listener.
     * @param listener
     */
    public void removeContextListener(IContextProviderListener listener);
    
    /**
     * Starts a specific processor (a unit processing commands) for the given context type.
     * (e.g. UI, server).
     * 
     * If installing an async processor, the UI processor will forward its commands to this 
     * context, in order to communicate to the server.
     * @param connection
     */
    public void installContext (ContextType type, UUID id, String name);
    
    /**
     * Drops a processor.
     */
    public void dropContext (ContextType type);
    
    /**
     * Checks if there is a context installed for the given type.
     */
    public boolean isContextSet (ContextType type);
    
}
