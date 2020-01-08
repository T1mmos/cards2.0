package gent.timdemey.cards.services.context;

import java.util.ConcurrentModificationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.swing.SwingUtilities;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.services.ICommandExecutionService;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.execution.CommandExecutionThread;

/**
 * The thread-local context provider.
 * 
 * @author Timmos
 *
 */
public final class ContextService implements IContextService
{

    private final ConcurrentMap<ContextType, Context> fullContexts;

    public ContextService()
    {
        fullContexts = new ConcurrentHashMap<>();
    }

    @Override
    public Context getThreadContext()
    {
        ContextType type;

        if (SwingUtilities.isEventDispatchThread())
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
    public void initialize(ContextType type, ICommandExecutionService cmdExecService)
    {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());
        Preconditions.checkState(type == cmdExecService.getContextType());
        
        boolean trackChanges = type == ContextType.UI;
        Context context = new Context(type, cmdExecService, trackChanges);

        Context prev = fullContexts.putIfAbsent(type, context);
        if (prev != null)
        {
            throw new ConcurrentModificationException(
                    "Context concurrently installed by different thread for type " + type);
        }
    }

    @Override
    public void drop(ContextType type)
    {
        Context curr = fullContexts.remove(type);
        if (curr == null)
        {
            throw new IllegalStateException(
                    "Async processor currently unavailable, so cannot drop processor for type " + type);
        }
    }

    @Override
    public boolean isInitialized(ContextType type)
    {
        return fullContexts.containsKey(type);
    }
}
