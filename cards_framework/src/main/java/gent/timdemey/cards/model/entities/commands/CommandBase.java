package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.di.Container;
import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.entities.common.PayloadBase;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.model.net.TCP_Connection;
import gent.timdemey.cards.model.net.UDP_Source;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.di.IContainerService;
import gent.timdemey.cards.services.context.ICommandExecutor;

public abstract class CommandBase extends EntityBase
{
    private volatile TCP_Connection sourceTcpConnection;
    private volatile UDP_Source sourceUdp;
    private volatile UUID sourceId;
    private volatile String serialized;
    
    protected final IContainerService _ContainerService;
    protected final ICommandExecutor _CommandExecutor;
    protected final State _State;
    protected final Context _Context;
    protected final ContextType _ContextType;
    
    private final PayloadBase _Payload;
    
    protected CommandBase(Container container, PayloadBase payload)
    {
        super(payload.id);
        
        this._ContainerService = container.Get(IContainerService.class);
        this._CommandExecutor = container.Get(ICommandExecutor.class);
        this._Context = container.Get(Context.class);
        this._State = container.Get(State.class);
        this._ContextType = container.Get(ContextType.class);
        
        this._Payload = payload;
    }

    public abstract CanExecuteResponse canExecute();

    public abstract void execute();

    /**
     * Called after {@link #execute(State)} but only after the server has accepted the command.
     * Commands may implement logic that can only be run client-side after the server confirmed
     * that the command's logic has been accepted.
     * <p>A good example is revealing cards of a cardstack only after it was confirmed by the server
     * that the top card's removal was a valid action.     
     * @param state
     */
    public void onAccepted()
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

    public boolean canUndo()
    {
        return false;
    }

    public void undo()
    {
        throw new UnsupportedOperationException();
    }

    protected final void schedule(ContextType type, CommandBase cmd)
    {
        
        // recreate the command if it is intended for a different container, 
        // this way the correct dependencies are injected
        if (type != cmd._ContextType)
        {
            Container container = _ContainerService.get(type);
            Container scopedContainer  = container.Scope();
            scopedContainer.AddSingleton((Class<PayloadBase>) cmd._Payload.getClass(), cmd._Payload);
            CommandBase cmd_copy = scopedContainer.Get((Class<CommandBase>) cmd.getClass());
            container.Get(ICommandExecutor.class).schedule(cmd_copy);
        }
        else 
        {
            _ContainerService.get(type).Get(ICommandExecutor.class).schedule(cmd);
        }
        
    }

    /**
     * Immediately runs the given command on the current context. If on the
     * UI thread any dialog is shown, this call will block.
     * 
     * @param cmd
     */
    protected final void run(CommandBase cmd)
    {
        _CommandExecutor.run(cmd);
    }

    protected final void CheckContext(ContextType expected)
    {
        if (_ContextType != expected)
        {
            throw new IllegalStateException("This command must be executed under the context type '" + expected
                    + "', but current context type is: '" + _ContextType + "'");
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
