package gent.timdemey.cards.services.context;

import gent.timdemey.cards.ICardPlugin;

import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CommandHistory;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.state.State;

public class LimitedContext
{
    private final ContextType contextType;
    private final ICommandExecutor cmdExecServ;
    private final State state;
    private final ICardPlugin _CardPlugin;

    public LimitedContext(
            ICardPlugin cardPlugin, 
            ICommandExecutor cmdExecServ, 
            ContextType contextType)
    {
        if (contextType == null)
        {
            throw new IllegalArgumentException("contextType");
        }
        if (cmdExecServ == null)
        {
            throw new IllegalArgumentException("cmdExecServ");
        }
        
        this._CardPlugin = cardPlugin;
        this.cmdExecServ = cmdExecServ;
        this.contextType = contextType;
        
        this.state = _CardPlugin.createState();
    }

    public ContextType getContextType()
    {
        return contextType;
    }
    
    public CanExecuteResponse canExecute (CommandBase command)
    {
        if (command.getSourceId() == null)
        {
            command.setSourceId(state.getLocalId());
        }
        CanExecuteResponse resp = command.canExecute(state);
        
        return resp;
    }
    
    public void schedule(CommandBase command)
    {
        if (command.getSourceId() == null)
        {
            command.setSourceId(state.getLocalId());
        }
        cmdExecServ.schedule(command, state);
    }

    public void run(CommandBase command)
    {
        if (command.getSourceId() == null)
        {
            command.setSourceId(state.getLocalId());
        }
        cmdExecServ.run(command, state);
    }
    
    void addExecutionListener(IExecutionListener executionListener)
    {
        cmdExecServ.addExecutionListener(executionListener);
    }
    
    void removeExecutionListener(IExecutionListener executionListener)
    {
        cmdExecServ.removeExecutionListener(executionListener);
    }

    State getState()
    {
        return state;
    }
    
    void shutdownAndWait()
    {
        this.cmdExecServ.shutdown();
    }

    public CommandHistory getCommandHistory()
    {
        return state.getCommandHistory();
    }
}
