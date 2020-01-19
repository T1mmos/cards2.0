package gent.timdemey.cards.services.context;

enum CommandExecutionState
{
    /**
     * Command history entry has been created but it's not executed yet.
     */
    Created,

    /**
     * Unexecuted after having been executed.
     */
    Unexecuted,

    /**
     * Executed, not yet locally confirmed.
     */
    Executed,

    /**
     * Executed, locally confirmed, and externally confirmed by the server.
     */
    Confirmed
}
