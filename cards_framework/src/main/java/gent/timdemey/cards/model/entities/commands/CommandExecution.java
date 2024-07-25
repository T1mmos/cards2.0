package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.delta.Property;
import gent.timdemey.cards.model.delta.StateValueRef;
import gent.timdemey.cards.utils.Debug;

public final class CommandExecution extends EntityBase
{
    public static final Property<CommandExecutionState> CommandExecutionState = Property.of(CommandExecution.class, CommandExecutionState.class, "CommandExecutionState");

    public final StateValueRef<CommandExecutionState> cmdExecutionState;
    
    private final CommandBase command;
    
    public CommandExecution(CommandBase command, CommandExecutionState cmdExecutionState)
    {
        this.command = command;
        this.cmdExecutionState = new StateValueRef<>(CommandExecutionState, id, cmdExecutionState);
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
