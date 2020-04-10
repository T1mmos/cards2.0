package gent.timdemey.cards.services;

import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.context.LimitedContext;

/**
 * Provides thread-based context e.g. data model and a command processor. You
 * can request limited context if you are not the owning thread of the requested
 * context; this gives you at least a command processor of the other context
 * which you can use to push commands to other layers.
 * 
 * @author Timmos
 */
public interface IContextService
{
    /**
     * Checks if the current thread is the UI thread. The base implementation 
     * checks if the current thread is the EDT. Test implementations may override
     * this method and always return true, or return true in case the thread
     * is the same one that started a unit test, etc. 
     * @return
     */
    public boolean isUiThread();

    /**
     * Returns the context based on the current thread. A thread has always full
     * access to its context, meaning that modifications to state are thread safe
     * because of thread-locality.
     * 
     * @return
     */
    public Context getThreadContext();

    /**
     * Returns the limited context of a given type, so a thread can schedule
     * commands which will run on a different context / thread.
     * 
     * @param ctxtType
     * @return
     */
    public LimitedContext getContext(ContextType ctxtType);

    /**
     * Checks if the given context type is referring to the current context.
     * 
     * @param ctxtType
     * @return
     */
    public boolean isCurrentContext(ContextType ctxtType);

    /**
     * Starts a context for the given context type (e.g. UI, server).
     * 
     * @param connection
     */
    public void initialize(ContextType type);

    /**
     * Drops a context.
     */
    public void drop(ContextType type);

    /**
     * Checks if there is a context installed for the given context type.
     */
    public boolean isInitialized(ContextType type);

}
