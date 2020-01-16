package gent.timdemey.cards.model.commands;

enum CommandExecutionState
{
    /**
     * Command history entry has been created but it's not put in the queue yet.
     */
    Created,

    /**
     * Entered the queue, ready for execution.
     */
    Submitted,

    /**
     * Unexecuted after having been executed.
     */
    Unexecuted,

    /**
     * Executed, not yet locally confirmed.
     */
    Executed,

    /**
     * Executed and locally confirmed, meaning that the new state is valid. This is
     * useful when executing composite commands.
     */
    Valid,

    /**
     * Executed, locally confirmed, and externally confirmed by the server.
     */
    Confirmed
}
