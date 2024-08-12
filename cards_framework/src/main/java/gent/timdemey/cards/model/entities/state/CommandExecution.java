package gent.timdemey.cards.model.entities.state;

import gent.timdemey.cards.model.delta.IChangeTracker;
import gent.timdemey.cards.model.delta.Property;
import gent.timdemey.cards.model.delta.StateValueRef;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CommandExecutionState;
import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.entities.state.payload.P_CommandExecution;
import gent.timdemey.cards.utils.Debug;

public final class CommandExecution extends EntityBase<P_CommandExecution>
{
    public static final Property<CommandExecutionState> CommandExecutionState = Property.of(CommandExecution.class, CommandExecutionState.class, "CommandExecutionState");

    public final StateValueRef<CommandExecutionState> cmdExecutionState;
    
    private final CommandBase command;
    
    public CommandExecution(
        IChangeTracker changeTracker,        
        P_CommandExecution parameters)
    {
        super(parameters);
        this.command = parameters.command;
        this.cmdExecutionState = new StateValueRef<>(changeTracker, CommandExecutionState, id, parameters.cmdExecutionState);
    }

    public CommandBase getCommand()
    {
        return command;
    }

    public CommandExecutionState getExecutionState()
    {
        return cmdExecutionState.get();
    }

    public void setExecutionState(CommandExecutionState state)
    {
        this.cmdExecutionState.set(state);
    }
    
    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("command", command.getClass().getSimpleName()) +
               Debug.getKeyValue("commandId", command.id) + 
               Debug.getKeyValue("cmdExecutionState", cmdExecutionState.get());
            
    }
}