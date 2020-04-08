package gent.timdemey.cards.services.context;

enum CommandExecutionState
{
    /**
     * Command history entry has been created but it's not executed yet.
     */
    Created,

    /**
     * Unexecuted after having been executed - single player only.
     */
    Unexecuted,
    
    /**
     * Executed - single player only.
     */
    Executed,
    
    /**
     * The command could not be reexecuted. This indicates that a command was either erased or inserted
     * in the command chain, and this command is now no longer executable.
     */
    Fail,
    
    /**
     * Executed, but awaiting confirmation from the server. A command in this state
     * cannot be undone by the user, but only by the server that doesn't accept the command.
     */
    AwaitingConfirmation,

    /**
     * Executed and accepted by the server.
     */
    Accepted,
    
    /**
     * Unexecuted because the server didn't accept the command. This is a final state.
     */
    Rejected,
}
