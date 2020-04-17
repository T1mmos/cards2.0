package gent.timdemey.cards.services.context;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.commands.C_Accept;
import gent.timdemey.cards.model.entities.commands.C_Reject;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.D_ReexecutionFail;
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
        IContextService ctxtServ = Services.get(IContextService.class);
        if(!ctxtServ.isUiThread())
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
                // multiplayer, the command is not yet accepted by the server
                state.getCommandHistory().addAwaiting(command, state);
            }
            else if (src_local && !hasServer)
            {
                // single player, so the command is immediately in executed state
                state.getCommandHistory().addExecuted(command, state);
            }
            else
            {
                // this command comes from the server so must be accepted
                List<CommandExecution> fails = state.getCommandHistory().addAccepted(command, state);
                HandleReexecutionFails(fails);
            }
        }
        else if (command instanceof C_Reject)
        {
            // this command comes from the server and references another command that got rejected
            C_Reject rejectCmd = (C_Reject) command;
            List<CommandExecution> fails = state.getCommandHistory().reject(rejectCmd.rejectedCommandId, state);
            HandleReexecutionFails(fails);
        }
        else if (command instanceof C_Accept)
        {
            // this command comes from the server and references another command that got accepted
            C_Accept acceptCmd = (C_Accept) command;
            state.getCommandHistory().accept(acceptCmd.acceptedCommandId, state);            
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
        if (fails.size() > 0)
        {
            D_ReexecutionFail cmd = new D_ReexecutionFail(fails);
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
