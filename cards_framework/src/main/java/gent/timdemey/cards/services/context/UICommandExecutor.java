package gent.timdemey.cards.services.context;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.commands.CommandBase;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.ICommandService;
import gent.timdemey.cards.services.IContextService;

class UICommandExecutor implements ICommandExecutor
{
    private final CommandHistory commandHistory;

    private final List<IExecutionListener> executionListeners;

    public UICommandExecutor()
    {
        ICardPlugin plugin = Services.get(ICardPlugin.class);
        boolean isMultiplayer = plugin.getPlayerCount() > 1;
        boolean undoable = !isMultiplayer;
        boolean erasable = isMultiplayer;
        
        this.commandHistory = new CommandHistory(undoable, erasable);
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
        ICommandService cmdServ = Services.get(ICommandService.class);
        boolean addToHistory = cmdServ.isSyncedCommand(command);
        
        boolean local = command.getSourceId() == state.getLocalId();
        boolean server = command.getSourceId() == state.getServerId();
        
        if (!local && !server)
        {
            throw new IllegalArgumentException("A command's source must either be local or the server");
        }
        if (local && !command.canExecute(state))
        {
            // if the command is locally created then it is supposed to be able to execute
            throw new IllegalStateException("The command '" + command.getClass().getSimpleName() + " cannot be executed");
        }
        
        if (addToHistory)
        {
            // delegate command execution to the command history
            if (local)
            {
                CommandExecution cmdExecution = new CommandExecution(command, CommandExecutionState.Executed);
                commandHistory.add(cmdExecution);
            }
            else
            {
                CommandExecution cmdExecution = new CommandExecution(command, CommandExecutionState.Accepted);
                List<CommandExecution> fails = commandHistory.inject(cmdExecution, state);
                HandleReexecutionFails(fails);
            }
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

    @Override
    public CommandHistory getCommandHistory()
    {
        return commandHistory;
    }
}
