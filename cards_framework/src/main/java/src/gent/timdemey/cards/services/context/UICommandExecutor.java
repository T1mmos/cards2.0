package gent.timdemey.cards.services.context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.SwingUtilities;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.C_Accept;
import gent.timdemey.cards.model.entities.commands.C_Reject;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CommandExecution;
import gent.timdemey.cards.model.entities.commands.CommandType;
import gent.timdemey.cards.model.entities.commands.D_OnReexecutionFail;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.contract.ExecutionState;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.interfaces.IContextService;

class UICommandExecutor implements ICommandExecutor
{
    private final List<IExecutionListener> executionListeners;

    public UICommandExecutor()
    {
        this.executionListeners = new ArrayList<>();
    }

    @Override
    public void schedule(CommandBase command, State state)
    {
        SwingUtilities.invokeLater(() -> execute(command, state));
    }

    @Override
    public void run(CommandBase command, State state)
    {
        IContextService ctxtServ = Services.get(IContextService.class);
        ContextType type = ctxtServ.getThreadContext().getContextType();
        if(type != ContextType.UI)
        {
            throw new IllegalStateException("Can only run a command from the UI thread. Use schedule instead when posting command from another thread!");
        }

        execute(command, state);
    }

    private void execute(CommandBase command, State state)
    {
        Logger.info("Executing '%s', id=%s...", command.getName(), command.id);

        CommandType cmdType = command.getCommandType();
        UUID localId = state.getLocalId();
        UUID serverId = state.getServerId();
        UUID cmdSrcId = command.getSourceId();
        
        boolean src_local = cmdSrcId == null || cmdSrcId.equals(localId);
        boolean src_server = cmdSrcId != null && cmdSrcId.equals(serverId);
        boolean hasServer = serverId != null;

        if(!src_local && !src_server)
        {
            throw new IllegalArgumentException("A command's source must either be local or the server");
        }

        CanExecuteResponse resp = command.canExecute(state);
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
                state.getCommandHistory().addAwaiting(command, state);
            }
            else
            {
                // this command comes from the server so must be accepted
                List<CommandExecution> fails = state.getCommandHistory().addAccepted(command, state);
                HandleReexecutionFails(fails);
            }
        }
        else if(cmdType == CommandType.TRACKED)
        {
            if(src_local && !hasServer)
            {
                // single player, so the command is immediately in executed state
                state.getCommandHistory().addExecuted(command, state);
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
            List<CommandExecution> fails = state.getCommandHistory().reject(rejectCmd.rejectedCommandId, state);
            HandleReexecutionFails(fails);
        }
        else if(command instanceof C_Accept)
        {
            // this command comes from the server and references another command that got
            // accepted
            C_Accept acceptCmd = (C_Accept) command;
            state.getCommandHistory().accept(acceptCmd.acceptedCommandId, state);
        }
        else
        {
            // execute the command here
            command.execute(state);
            command.onAccepted(state);
        }

        // update the listeners
        for (IExecutionListener execListener : executionListeners)
        {
            execListener.onExecuted(command);
        }
        
        command.onExecuted();
    }

    private void HandleReexecutionFails(List<CommandExecution> fails)
    {
        if(fails.size() > 0)
        {
            D_OnReexecutionFail cmd = new D_OnReexecutionFail(fails);
            IContextService ctxtServ = Services.get(IContextService.class);
            ctxtServ.getThreadContext().schedule(cmd);
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
        if(!Services.get(IContextService.class).isCurrentContext(ContextType.UI))
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
