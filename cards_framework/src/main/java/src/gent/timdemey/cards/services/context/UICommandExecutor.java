package gent.timdemey.cards.services.context;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.commands.C_Accept;
import gent.timdemey.cards.model.entities.commands.C_Reject;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.IContextService;

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
        if(!SwingUtilities.isEventDispatchThread())
        {
            SwingUtilities.invokeLater(() -> schedule(command, state));
            return;
        }

        execute(command, state);
    }

    private void execute(CommandBase command, State state)
    {        
        boolean syncable = command.isSyncable();        
        boolean src_local = command.getSourceId() == state.getLocalId();
        boolean src_server = command.getSourceId() == state.getServerId();
        boolean hasServer = state.getServerId() != null;
        
        if (!src_local && !src_server)
        {
            throw new IllegalArgumentException("A command's source must either be local or the server");
        }
        if (src_local && !command.canExecute(state))
        {
            // if the command is locally created then it is supposed to be able to execute
            throw new IllegalStateException("The command '" + command.getClass().getSimpleName() + " cannot be executed");
        }
        
        if (syncable)
        {
            // delegate command execution to the command history
            if (src_local && hasServer)
            {
                // multiplayer
                CommandExecution cmdExecution = new CommandExecution(command, CommandExecutionState.AwaitingConfirmation);
                state.getCommandHistory().add(cmdExecution, state);
            }
            else if (src_local && !hasServer)
            {
                // single player
                CommandExecution cmdExecution = new CommandExecution(command, CommandExecutionState.Executed);
                state.getCommandHistory().add(cmdExecution, state);
            }
            else
            {
                // this command comes from the server
                CommandExecution cmdExecution = new CommandExecution(command, CommandExecutionState.Accepted);
                List<CommandExecution> fails = state.getCommandHistory().inject(cmdExecution, state);
                HandleReexecutionFails(fails);
            }
        }
        else if (command instanceof C_Reject)
        {
            // this command comes from the server and indicates that another command is rejected
            C_Reject rejectCmd = (C_Reject) command;
            List<CommandExecution> fails = state.getCommandHistory().erase(rejectCmd.rejectedCommandId, state);
            HandleReexecutionFails(fails);
        }
        else if (command instanceof C_Accept)
        {
            // this command comes from the server and indicates that another command is rejected
            C_Accept acceptCmd = (C_Accept) command;
            state.getCommandHistory().accept(acceptCmd.acceptedCommandId);            
        }
        else 
        {
            // execute the command here
            command.execute(state);
        }
        
        // update the listeners
        for (IExecutionListener execListener : executionListeners)
        {
            execListener.onExecuted();
        }
    }
    
    private void HandleReexecutionFails(List<CommandExecution> fails)
    {
        // TODO show a dialog telling the user some of his command were revoked because
        // the server inserted a command in the history, that now renders some of his 
        // commands invalid, given the changed state
        
        
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
}
