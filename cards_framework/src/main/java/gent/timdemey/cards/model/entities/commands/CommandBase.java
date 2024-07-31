package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.entities.common.PayloadBase;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.model.net.TCP_Connection;
import gent.timdemey.cards.model.net.UDP_Source;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.context.LimitedContext;
import gent.timdemey.cards.services.interfaces.IContextService;

public abstract class CommandBase extends EntityBase
{
    private volatile TCP_Connection sourceTcpConnection;
    private volatile UDP_Source sourceUdp;
    private volatile UUID sourceId;
    private volatile String serialized;
    
    protected final IContextService _ContextService;
    

    protected CommandBase(IContextService contextService, PayloadBase payload)
    {
        super(payload.id);
        
        this._ContextService = contextService;
    }

    private final Context getContext()
    {
        Context context = _ContextService.getThreadContext();
        return context;
    }

    public final CanExecuteResponse canExecute(State state)
    {
        Context context = getContext();
        CanExecuteResponse response = canExecute(context, context.getContextType(), state);
        
        if (response == null)
        {
            throw new NullPointerException("canExecute must return a CanExecuteResponse but got null");
        }
        
        return response;
    }

    protected abstract CanExecuteResponse canExecute(Context context, ContextType type, State state);

    public final void execute(State state)
    {
        Context context = getContext();
        execute(context, context.getContextType(), state);
    }

    protected abstract void execute(Context context, ContextType type, State state);

    /**
     * Called after {@link #execute(State)} but only after the server has accepted the command.
     * Commands may implement logic that can only be run client-side after the server confirmed
     * that the command's logic has been accepted.
     * <p>A good example is revealing cards of a cardstack only after it was confirmed by the server
     * that the top card's removal was a valid action.     
     * @param state
     */
    public final void onAccepted(State state)
    {
        Context context = getContext();
        onAccepted(context, context.getContextType(), state);
    }

    public void onAccepted(Context context, ContextType type, State state)
    {
        // override when necessary
    }
    
    /**
     * Called after this command's logic ({@link #execute(State)} and {@link #onAccepted(State)}) has completely run
     * and all execution listeners have been notified.
     * This method should never alter state, as no execution listeners would be notified about such change(s). For
     * example, this method can be implemented to spawn a dialog after all listeners have run their logic.
     */
    public void onExecuted()
    {
        // override when necessary
    }

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

    protected final void schedule(ContextType type, CommandBase cmd)
    {
        LimitedContext context = _ContextService.getContext(type);
        context.schedule(cmd);
    }

    /**
     * Immediately runs the given command on the current context thread. If on the
     * UI thread any dialog is shown, this call will block.
     * 
     * @param cmd
     */
    protected final void run(CommandBase cmd)
    {
        ContextType ctxtType = _ContextService.getThreadContext().getContextType();
        LimitedContext context = _ContextService.getContext(ctxtType);
        context.run(cmd);
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
            throw new IllegalStateException("This command must be executed under the context type " + expected
                    + ", but current context type is: " + current);
        }
    }

    public void setSourceTcpConnection(TCP_Connection tcpConnection)
    {
        this.sourceTcpConnection = tcpConnection;
    }

    protected final TCP_Connection getSourceTcpConnection()
    {
        return sourceTcpConnection;
    }

    public void setSourceUdp(UDP_Source udpSource)
    {
        this.sourceUdp = udpSource;
    }

    protected final UDP_Source getSourceUdp()
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
     * Indicates if this command should be added to the undo/redo list. By default,
     * this returns false, so commands will by default not be undoable/redoable
     * unless you override this method.
     * 
     * @return
     */
    public CommandType getCommandType()
    {
        return CommandType.DEFAULT;
    }

    @Override
    public String toDebugString()
    {
        return "CommandBase (" + getName() + ")- override this method to provide more info";
    }

    public String getName()
    {
        return this.getClass().getSimpleName();
    }
}
