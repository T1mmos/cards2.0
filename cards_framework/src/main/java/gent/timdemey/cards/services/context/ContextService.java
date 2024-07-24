package gent.timdemey.cards.services.context;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.di.Container;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.swing.SwingUtilities;

import gent.timdemey.cards.services.interfaces.IContextService;

/**
 * The thread-local context provider.
 * 
 * @author Timmos
 *
 */
public class ContextService implements IContextService
{
    protected final ConcurrentMap<ContextType, Context> fullContexts;
    private final Set<IContextListener> contextListeners;
    private final ICardPlugin _CardPlugin;
    private final IContextService _ContextService;
    private final Container _Container;

    public ContextService(Container container, ICardPlugin cardPlugin, IContextService contextService)
    {
        this._Container = container;
        this._CardPlugin = cardPlugin;
        this._ContextService = contextService;
        fullContexts = new ConcurrentHashMap<>();
        contextListeners = Collections.synchronizedSet(new HashSet<>());
    }

    @Override
    public boolean isUiThread()
    {
        return SwingUtilities.isEventDispatchThread();
    }

    @Override
    public Context getThreadContext()
    {
        ContextType type;

        if (isUiThread())
        {
            type = ContextType.UI;
        }
        else if (Thread.currentThread() instanceof CommandExecutionThread)
        {
            CommandExecutionThread cmdExecThread = (CommandExecutionThread) Thread.currentThread();
            type = cmdExecThread.contextType;
        }
        else
        {
            throw new IllegalThreadStateException(
                    "This thread has no access to context: " + Thread.currentThread().getName());
        }

        Context context = fullContexts.get(type);
        if (context == null)
        {
            throw new IllegalStateException("No context installed for context type: " + type);
        }
        return context;
    }

    @Override
    public boolean isCurrentContext(ContextType ctxtType)
    {
        return getThreadContext().getContextType() == ctxtType;
    }

    @Override
    public LimitedContext getContext(ContextType ctxtType)
    {
        Context context = fullContexts.get(ctxtType);
        if (context == null)
        {
            throw new IllegalStateException("No context installed for context type: " + ctxtType);
        }
        return context.limitedContext;
    }

    @Override
    public void initialize(ContextType type)
    {
        if (!isUiThread())
        {
            throw new IllegalStateException("You must initialize from the UI thread!");
        }

        ICommandExecutor cmdExecutor = null;
        if (type == ContextType.UI)
        {
            cmdExecutor = _Container.Get(UICommandExecutor.class);
        }
        else if (type == ContextType.Server)
        {
            cmdExecutor = _Container.Get(ServerCommandExecutor.class);
        }
        else
        {
            throw new IllegalArgumentException("Unknown ContextType: " + type);
        }

        boolean allowListeners = type == ContextType.UI;
        Context context = Context.createContext(_CardPlugin, cmdExecutor, _ContextService, type, allowListeners);

        Context prev = fullContexts.putIfAbsent(type, context);
        if (prev != null)
        {
            throw new ConcurrentModificationException(
                    "Context concurrently installed by different thread for type " + type);
        }
        
        synchronized (contextListeners)
        {
            for (IContextListener ctxtListener : contextListeners)
            {
                ctxtListener.onContextInitialized(type);
            }
        }
    }

    @Override
    public void drop(ContextType type)
    {
        Context ctxt = fullContexts.get(type);
        ctxt.limitedContext.shutdownAndWait();
        Context curr = fullContexts.remove(type);
        if (curr == null)
        {
            throw new IllegalStateException(
                    "Async processor currently unavailable, so cannot drop processor for type " + type);
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
        return fullContexts.containsKey(type);
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
