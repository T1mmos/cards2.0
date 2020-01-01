package gent.timdemey.cards.model.commands;

public final class CommandEntry
{
    private final CommandBase command;
    private CommandExecutionState state;
    
    CommandEntry (CommandBase command)
    {
        this.command = command;
        this.state = CommandExecutionState.Created;
    }
    
    CommandBase getCommand()
    {
        return command;
    }
    
    CommandExecutionState getExecutionState()
    {
        return state;
    }
    
    void setExecutionState (CommandExecutionState state)
    {
        this.state = state;
    }
}
