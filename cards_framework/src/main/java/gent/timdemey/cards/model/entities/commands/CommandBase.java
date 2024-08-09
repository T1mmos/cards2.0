package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.di.Container;
import java.util.UUID;

import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.model.net.TCP_Connection;
import gent.timdemey.cards.model.net.UDP_Source;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.di.IContainerService;
import gent.timdemey.cards.model.net.TCP_ConnectionPool;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.context.ICommandExecutor;
import java.util.List;

public abstract class CommandBase<CMDPAYLOAD extends CommandPayloadBase> extends EntityBase
{
    private volatile TCP_Connection sourceTcpConnection;
    private volatile UDP_Source sourceUdp;
    
    protected final IContainerService _ContainerService;
    protected final ICommandExecutor _CommandExecutor;
    protected final State _State;
    protected final Context _Context;
    protected final ContextType _ContextType;
    protected final CommandDtoMapper _CommandDtoMapper;
    
    public final UUID creatorId;
    public final ContextType creatorContextType;
    protected final CMDPAYLOAD _Payload;
    
    protected CommandBase(Container container, CMDPAYLOAD payload)
    {
        super(payload.id);
        
        if (payload.creatorContextType == null)
        {
            throw new IllegalArgumentException("creatorContextType");
        }
        
        this._ContainerService = container.Get(IContainerService.class);
        this._CommandExecutor = container.Get(ICommandExecutor.class);
        this._Context = container.Get(Context.class);
        this._State = container.Get(State.class);
        this._ContextType = container.Get(ContextType.class);
        this._CommandDtoMapper = container.Get(CommandDtoMapper.class);
        
        this._Payload = payload;
        this.creatorId = payload.creatorId;
        this.creatorContextType = payload.creatorContextType;
    }

    public abstract CanExecuteResponse canExecute();

    public abstract void execute();

    /**
     * Called after {@link #execute(State)} but only after the server has accepted the command.
     * Commands may implement logic that can only be run client-side after the server confirmed
     * that the command's logic has been accepted.
     * <p>A good example is revealing cards of a cardstack only after it was confirmed by the server
     * that the top card's removal was a valid action.     
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
        throw new UnsupportedOperationException("Undo logic must be provided in the concrete implementing class");
    }

    protected final void schedule(ContextType type, CommandBase cmd)
    {
        // recreate the command if it is intended for a different container, 
        // this way the correct dependencies are injected
        if (type != cmd._ContextType)
        {
            Container container = _ContainerService.get(type);
            Container scopedContainer  = container.Scope();
            scopedContainer.AddSingleton((Class<Object>)(Class<?>) cmd._Payload.getClass(), cmd._Payload);
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

    protected final void send(UUID remoteId, CommandBase cmd)
    {
        TCP_Connection connection = _State.getTcpConnectionPool().getConnection(remoteId);
        String msg = _CommandDtoMapper.toJson(cmd);
        connection.send(msg);
    }
    
    protected final void send(List<UUID> remoteIds, CommandBase cmd)
    {
        TCP_ConnectionPool pool = _State.getTcpConnectionPool();
        String msg = _CommandDtoMapper.toJson(cmd);
        for (UUID remoteId : remoteIds)
        {
            TCP_Connection connection = pool.getConnection(remoteId);
            connection.send(msg);
        }
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
