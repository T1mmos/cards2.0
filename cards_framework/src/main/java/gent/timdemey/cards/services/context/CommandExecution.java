package gent.timdemey.cards.services.context;

import gent.timdemey.cards.model.commands.CommandBase;

public final class CommandExecution
{
    private final CommandBase command;
    private CommandExecutionState state;

    CommandExecution(CommandBase command)
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

    void setExecutionState(CommandExecutionState state)
    {
        this.state = state;
    }
}
