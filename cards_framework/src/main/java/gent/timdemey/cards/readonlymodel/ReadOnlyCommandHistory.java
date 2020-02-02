package gent.timdemey.cards.readonlymodel;

import gent.timdemey.cards.services.context.CommandHistory;

public class ReadOnlyCommandHistory extends ReadOnlyEntityBase<CommandHistory>
{
    public static final ReadOnlyProperty<Integer> CurrentIndex =  ReadOnlyProperty.of(CommandHistory.CurrentIndex);
    public static final ReadOnlyProperty<Integer> AcceptedIndex =  ReadOnlyProperty.of(CommandHistory.AcceptedIndex);
    
    
    ReadOnlyCommandHistory(CommandHistory entity)
    {
        super(entity);
    } 

}
