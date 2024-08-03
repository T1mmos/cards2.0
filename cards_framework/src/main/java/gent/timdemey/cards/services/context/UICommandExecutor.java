package gent.timdemey.cards.services.context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.SwingUtilities;


import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.C_Accept;
import gent.timdemey.cards.model.entities.commands.C_Reject;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CommandFactory;
import gent.timdemey.cards.model.entities.state.CommandExecution;
import gent.timdemey.cards.model.entities.commands.CommandType;
import gent.timdemey.cards.model.entities.commands.C_ShowReexecutionFail;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.contract.ExecutionState;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.interfaces.IContextService;

public class UICommandExecutor implements ICommandExecutor
{
    private final List<IExecutionListener> executionListeners;
    private final IContextService _ContextService;
    private final Logger _Logger;
    private final CommandFactory _CommandFactory;
    private final State _State;

    public UICommandExecutor(
        IContextService contextService,
        CommandFactory commandFactory,
        State state,
        Logger logger)
    {
        this._ContextService = contextService;
        this._CommandFactory = commandFactory;
        this._Logger = logger;
        this._State = state;
        
        this.executionListeners = new ArrayList<>();        
    }

    @Override
    public void schedule(CommandBase command)
    {
        SwingUtilities.invokeLater(() -> execute(command));
    }

    @Override
    public void run(CommandBase command)
    {
        ContextType type = _ContextService.getThreadContext().getContextType();
        if(type != ContextType.UI)
        {
            throw new IllegalStateException("Can only run a command from the UI thread. Use schedule instead when posting command from another thread!");
        }

        execute(command);
    }

    private void execute(CommandBase command)
    {
        _Logger.info("Executing '%s', id=%s...", command.getName(), command.id);

        CommandType cmdType = command.getCommandType();
        UUID localId = _State.getLocalId();
        UUID serverId = _State.getServerId();
        UUID cmdSrcId = command.getSourceId();
        
         boolean src_local = cmdSrcId == null || cmdSrcId.equals(localId);
        boolean src_server = cmdSrcId != null && cmdSrcId.equals(serverId);
        boolean hasServer = serverId != null;

        if(!src_local && !src_server)
        {
            throw new IllegalArgumentException("A command's source must either be local or the server");
        }

        CanExecuteResponse resp = command.canExecute();
        if (resp.execState == ExecutionState.Error)
        {
            throw new IllegalStateException("The command '" + command.getName() + "' cannot be executed because of a state error, reason: " + resp.reason);
        }
        
        if(src_local && !resp.canExecute())
        {
            // if the command is locally created then it is supposed to be able to execute
            throw new IllegalStateException("The command '" + command.getName() + "' cannot be executed, reason: " + resp.reason);
        }

        if(cmdType == CommandType.SYNCED)
        {
            if(!hasServer)
            {
                throw new UnsupportedOperationException("The command "+command.getName()+" of type " + cmdType + " cannot be handled if no server is set");
            }
            // delegate command execution to the command history
            if(src_local)
            {
                // multiplayer, the command is not yet accepted by the server
                _State.getCommandHistory().addAwaiting(command);
            }
            else
            {
                // this command comes from the server so must be accepted
                List<CommandExecution> fails = _State.getCommandHistory().addAccepted(command, _State);
                HandleReexecutionFails(fails);
            }
        }
        else if(cmdType == CommandType.TRACKED)
        {
            if(src_local && !hasServer)
            {
                // single player, so the command is immediately in executed state
                _State.getCommandHistory().addExecuted(command);
            }
            else
            {
                throw new UnsupportedOperationException("A command of type " + cmdType + " cannot be handled in this case (or implement)");
            }
        }
        else if(command instanceof C_Reject)
        {
            // this command comes from the server and references another command that got
            // rejected
            C_Reject rejectCmd = (C_Reject) command;
            List<CommandExecution> fails = _State.getCommandHistory().reject(rejectCmd.rejectedCommandId);
            HandleReexecutionFails(fails);
        }
        else if(command instanceof C_Accept)
        {
            // this command comes from the server and references another command that got
            // accepted
            C_Accept acceptCmd = (C_Accept) command;
            _State.getCommandHistory().accept(acceptCmd.acceptedCommandId);
        }
        else
        {
            // execute the command here
            command.execute();
            command.onAccepted();
        }

        // update the listeners
        for (IExecutionListener execListener : executionListeners)
        {
            execListener.onExecuted();
        }
        
        command.onExecuted();
    }

    private void HandleReexecutionFails(List<CommandExecution> fails)
    {
        if(fails.size() > 0)
        {
            C_ShowReexecutionFail cmd = _CommandFactory.ShowDialog_ReexecutionFail(fails);
            _ContextService.getThreadContext().schedule(cmd);
        }
    }

    @Override
    public void addExecutionListener(IExecutionListener executionListener)
    {
        if(executionListeners.contains(executionListener))
        {
            throw new IllegalStateException("This Execution Listener is already registered");
        }

        this.executionListeners.add(executionListener);
    }

    @Override
    public void removeExecutionListener(IExecutionListener executionListener)
    {
        // check that the execution listener is installed on the UI thread
        if(!_ContextService.isCurrentContext(ContextType.UI))
        {
            throw new IllegalStateException("Execution listener should be set on the UI thread");
        }
        if(!executionListeners.contains(executionListener))
        {
            throw new IllegalStateException("This Execution Listener is not registered");
        }

        this.executionListeners.remove(executionListener);
    }

    @Override
    public void shutdown()
    {
        // can't shutdown the EDT
    }
}
