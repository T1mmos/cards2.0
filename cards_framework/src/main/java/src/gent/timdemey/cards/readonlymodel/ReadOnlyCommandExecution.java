package gent.timdemey.cards.readonlymodel;

import gent.timdemey.cards.model.entities.commands.CommandExecution;
import gent.timdemey.cards.model.entities.commands.CommandExecutionState;

public class ReadOnlyCommandExecution extends ReadOnlyEntityBase<CommandExecution>
{
    public static final ReadOnlyProperty<CommandExecutionState> CommandExecutionState = ReadOnlyProperty.of(CommandExecution.CommandExecutionState);
    
    ReadOnlyCommandExecution(CommandExecution entity)
    {
        super(entity);
    } 
}
