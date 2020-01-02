package gent.timdemey.cards.model.commands;

import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.EntityBase;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.context.LimitedContext;

public abstract class CommandBase extends EntityBase
{   
    protected CommandBase ()
    {
        super();
    }
    
    protected CommandBase(UUID id)
    {
        super(id);
    }
    
    private final Context getContext()
    {
        IContextService contextServ = Services.get(IContextService.class);
        Context context = contextServ.getThreadContext();
        return context;
    }
        
    public final boolean canExecute (State state)
    {
        Context context =  getContext();
        return canExecute(context, context.getContextType(), state);
    }
    
    protected abstract boolean canExecute(Context context, ContextType type, State state);
    
    public final void execute (State state)
    {
        Context context =  getContext();
        execute(context, context.getContextType(), state);
    }
    
    protected abstract void execute(Context context, ContextType type, State state);
    
    protected final boolean canUndo (State state)
    {
        Context context =  getContext();
        return canUndo(context, context.getContextType(), state);
    }
    
    protected boolean canUndo(Context context, ContextType type, State state)
    {
        return false;
    }
    
    public final void undo (State state)
    {
        Context context =  getContext();
        undo(context, context.getContextType(), state);
    }
    
    protected void undo(Context context, ContextType type, State state)
    {
        throw new UnsupportedOperationException();
    }
    
    
    protected final void reschedule (ContextType type)
    {
        IContextService contextServ = Services.get(IContextService.class);
        LimitedContext context = contextServ.getContext(type);
        context.schedule(this);
    }
}
