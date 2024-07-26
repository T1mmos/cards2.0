package gent.timdemey.cards.services.context;

import gent.timdemey.cards.ICardPlugin;

import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.state.CommandHistory;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.State;

public class LimitedContext
{
    private final ContextType _ContextType;
    private final ICommandExecutor _CommandExecutor;
    private final State _State;
    private final ICardPlugin _CardPlugin;

    public LimitedContext(
            ICardPlugin cardPlugin, 
            ICommandExecutor cmdExecServ, 
            State state,
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
        this._CommandExecutor = cmdExecServ;
        this._ContextType = contextType;        
        this._State = state;
    }

    public ContextType getContextType()
    {
        return _ContextType;
    }
    
    public CanExecuteResponse canExecute (CommandBase command)
    {
        if (command.getSourceId() == null)
        {
            command.setSourceId(_State.getLocalId());
        }
        CanExecuteResponse resp = command.canExecute(_State);
        
        return resp;
    }
    
    public void schedule(CommandBase command)
    {
        if (command.getSourceId() == null)
        {
            command.setSourceId(_State.getLocalId());
        }
        _CommandExecutor.schedule(command, _State);
    }

    public void run(CommandBase command)
    {
        if (command.getSourceId() == null)
        {
            command.setSourceId(_State.getLocalId());
        }
        _CommandExecutor.run(command, _State);
    }
    
    void addExecutionListener(IExecutionListener executionListener)
    {
        _CommandExecutor.addExecutionListener(executionListener);
    }
    
    void removeExecutionListener(IExecutionListener executionListener)
    {
        _CommandExecutor.removeExecutionListener(executionListener);
    }

    State getState()
    {
        return _State;
    }
    
    void shutdownAndWait()
    {
        this._CommandExecutor.shutdown();
    }

    public CommandHistory getCommandHistory()
    {
        return _State.getCommandHistory();
    }
}
