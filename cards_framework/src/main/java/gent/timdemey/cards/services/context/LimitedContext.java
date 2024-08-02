package gent.timdemey.cards.services.context;

import gent.timdemey.cards.ICardPlugin;

import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.State;

public class LimitedContext
{
    private final ContextType _ContextType;
    private final ICommandExecutor _CommandExecutor;
    private final State _State;

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
        CanExecuteResponse resp = command.canExecute();
        
        return resp;
    }
    
    public void schedule(CommandBase command)
    {
        if (command.getSourceId() == null)
        {
            command.setSourceId(_State.getLocalId());
        }
        _CommandExecutor.schedule(command);
    }

    public void run(CommandBase command)
    {
        if (command.getSourceId() == null)
        {
            command.setSourceId(_State.getLocalId());
        }
        _CommandExecutor.run(command);
    }
    
    void addExecutionListener(IExecutionListener executionListener)
    {
        _CommandExecutor.addExecutionListener(executionListener);
    }
    
    void removeExecutionListener(IExecutionListener executionListener)
    {
        _CommandExecutor.removeExecutionListener(executionListener);
    }
    
    void shutdownAndWait()
    {
        this._CommandExecutor.shutdown();
    }
}
