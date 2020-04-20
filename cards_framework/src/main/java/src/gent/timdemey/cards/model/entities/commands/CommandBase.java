package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.entities.common.PayloadBase;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.netcode.TCP_Connection;
import gent.timdemey.cards.netcode.UDP_Source;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.ISerializationService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.context.LimitedContext;

public abstract class CommandBase extends EntityBase
{
    private volatile TCP_Connection sourceTcpConnection;
    private volatile UDP_Source sourceUdp;
    private volatile UUID sourceId;
    private volatile String serialized;

    protected CommandBase()
    {
        super();
    }

    protected CommandBase(PayloadBase pl)
    {
        super(pl);
    }
    
    protected final void forward(ContextType type, State state)
    {
        if (type == ContextType.Server)
        {
            throw new IllegalStateException("To forward, we need to be in the UI or Client layer");
        }
        if (getSourceId() == null)
        {
            throw new IllegalStateException("To forward, we need to know the source ID of this command: " + this.getClass().getCanonicalName());
        }
        
        if (type == ContextType.Client)
        {
            if (getSourceId().equals(state.getServerId()))
            {
                // to UI
                reschedule(ContextType.UI);
            }
            else if (getSourceId().equals(state.getLocalId()))
            {
                // to Server
                String serialized = getCommandDtoMapper().toJson(this);            
                state.getTcpConnectionPool().getConnection(state.getServerId()).send(serialized);
            }
        }
        else if (type == ContextType.UI)
        {
            if (getSourceId().equals(state.getLocalId()))
            {
                // to server
                reschedule(ContextType.Client);
            }
        }
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

    public final boolean canUndo(State state)
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

    protected final void CheckContext(ContextType current, ContextType expected)
    {
        if (current != expected)
        {
            throw new IllegalStateException(
                "This command must be executed under the context type " + expected + ", but current context type is: " + current);
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
    
    public void setSourceUdp (UDP_Source udpSource)
    {
        this.sourceUdp = udpSource;
    }
    
    protected final UDP_Source getSourceUdp ()
    {
        return sourceUdp;
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

    /**
     * Indicates if this command should be added to the undo/redo list.
     * By default, this returns false, so commands will by default not be
     * undoable/redoable unless you override this method. 
     * @return
     */
    public boolean isSyncable()
    {
        return false;
    }
    
    protected static CommandDtoMapper getCommandDtoMapper()
    {
        return Services.get(ISerializationService.class).getCommandDtoMapper();
    }
    
    @Override
    public String toDebugString()
    {
        return "CommandBase ("+getClass().getSimpleName()+")- override this method to provide more info";
    }
}
