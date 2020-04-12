package gent.timdemey.cards.readonlymodel;

import gent.timdemey.cards.services.context.CommandExecution;
import gent.timdemey.cards.services.context.CommandExecutionState;

public class ReadOnlyCommandExecution extends ReadOnlyEntityBase<CommandExecution>
{
    public static final ReadOnlyProperty<CommandExecutionState> CommandExecutionState = ReadOnlyProperty.of(CommandExecution.CommandExecutionState);
    
    ReadOnlyCommandExecution(CommandExecution entity)
    {
        super(entity);
    } 
}
