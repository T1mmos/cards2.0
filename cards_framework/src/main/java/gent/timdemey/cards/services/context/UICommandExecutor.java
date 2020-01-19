package gent.timdemey.cards.services.context;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.commands.CommandBase;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.IContextService;

public class UICommandExecutor implements ICommandExecutor
{
    private final CommandHistory commandHistory;

    private final List<IExecutionListener> executionListeners;

    public UICommandExecutor()
    {
        this.commandHistory = new CommandHistory();
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
        if (command.getSourceId() == state.getLocalId())
        {
            // if the command is locally created then it is supposed to be able to execute
            if (!command.canExecute(state))
            {
                throw new IllegalStateException("The command '" + command.getClass().getSimpleName() + " cannot be executed");
            }
        }
        else if (command.getSourceId() == state.getServerId())
        {
            // a server command must always execute, and therefore we must rollback all 
            // unconfirmed command in order to insert this command in the command history 
            // at the correct location, to detect subsequent commands that become now invalid.
            
            commandHistory.getCommands(CommandExecutionState.Executed);
        }
        
        
        if(!command.canExecute(state))
        {
            
        }

        CommandExecution cmdExecution = new CommandExecution(command);
        command.execute(state);

        commandHistory.addCommandExecution(cmdExecution);

        for (IExecutionListener execListener : executionListeners)
        {
            execListener.onExecuted();
        }
    }

    @Override
    public void addExecutionListener(IExecutionListener executionListener)
    {
        // check that the execution listener is installed on the UI thread
        if(!Services.get(IContextService.class).isCurrentContext(ContextType.UI))
        {
            throw new IllegalStateException("Execution listener should be set on the UI thread");
        }
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
    public CommandHistory getCommandHistory()
    {
        return commandHistory;
    }
}
