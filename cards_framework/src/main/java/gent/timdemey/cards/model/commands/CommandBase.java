package gent.timdemey.cards.model.commands;

import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.EntityBase;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.multiplayer.io.TCP_Connection;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.context.LimitedContext;

public abstract class CommandBase extends EntityBase
{
    private volatile TCP_Connection sourceTcpConnection;
    private volatile UUID sourceId;
    private volatile String serialized;

    protected CommandBase()
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

    public final boolean canExecute(State state)
    {
        Context context = getContext();
        return canExecute(context, context.getContextType(), state);
    }

    protected abstract boolean canExecute(Context context, ContextType type, State state);

    public final void execute(State state)
    {
        Context context = getContext();
        execute(context, context.getContextType(), state);
    }

    protected abstract void execute(Context context, ContextType type, State state);

    protected final boolean canUndo(State state)
    {
        Context context = getContext();
        return canUndo(context, context.getContextType(), state);
    }

    protected boolean canUndo(Context context, ContextType type, State state)
    {
        return false;
    }

    public final void undo(State state)
    {
        Context context = getContext();
        undo(context, context.getContextType(), state);
    }

    protected void undo(Context context, ContextType type, State state)
    {
        throw new UnsupportedOperationException();
    }

    protected final void reschedule(ContextType type)
    {
        schedulePriv(type, this);
    }

    protected final void schedule(ContextType type, CommandBase cmd)
    {
        if (cmd == this)
        {
            throw new UnsupportedOperationException(
                    "To reschedule the current command, use reschedule - schedule is only for launching new commands.");
        }
        schedulePriv(type, cmd);
    }

    protected final void CheckNotContext(ContextType current, ContextType... types)
    {
        for (ContextType type : types)
        {
            if (current == type)
            {
                throw new IllegalStateException(
                        "This command cannot be executed under the current context type " + current);
            }
        }
    }

    private void schedulePriv(ContextType type, CommandBase cmd)
    {
        IContextService contextServ = Services.get(IContextService.class);
        LimitedContext context = contextServ.getContext(type);
        context.schedule(cmd);
    }

    public void setSourceTcpConnection(TCP_Connection tcpConnection)
    {
        this.sourceTcpConnection = tcpConnection;
    }

    protected final TCP_Connection getSourceTcpConnection()
    {
        return sourceTcpConnection;
    }

    public void setSourceId(UUID sourceId)
    {
        this.sourceId = sourceId;
    }

    public UUID getSourceId()
    {
        return sourceId;
    }

    public void setSerialized(String serialized)
    {
        this.serialized = serialized;
    }

    public String getSerialized()
    {
        if (serialized == null)
        {
            throw new IllegalStateException(
                    "No serialized form of this command was set, this can happen when the command did not enter via a TCP connection.");
        }
        return serialized;
    }
}
