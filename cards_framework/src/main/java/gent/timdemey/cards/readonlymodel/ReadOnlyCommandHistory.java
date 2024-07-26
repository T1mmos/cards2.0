package gent.timdemey.cards.readonlymodel;

import gent.timdemey.cards.model.entities.state.CommandHistory;

public class ReadOnlyCommandHistory extends ReadOnlyEntityBase<CommandHistory>
{
    public static final ReadOnlyProperty<Integer> CurrentIndex = ReadOnlyProperty.of(CommandHistory.CurrentIndex);
    public static final ReadOnlyProperty<Integer> AcceptedIndex = ReadOnlyProperty.of(CommandHistory.AcceptedIndex);
    public static final ReadOnlyProperty<ReadOnlyCommandExecution> ExecLine = ReadOnlyProperty.of(ReadOnlyCommandExecution.class, CommandHistory.ExecLine);
    
    ReadOnlyCommandHistory(CommandHistory entity)
    {
        super(entity);
    } 
}
