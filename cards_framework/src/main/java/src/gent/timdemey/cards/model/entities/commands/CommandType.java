package gent.timdemey.cards.model.entities.commands;

public enum CommandType
{
    /**
     * The command is not tracked in command history and thus it cannot be undone.
     */
    DEFAULT,
    
    /**
     * The command is tracked in command history but need not be undoable.
     */
    TRACKED,
    
    /**
     * The command must be in sync between server and client and therefore needs to be tracked
     * in command history, and must be undoable. The command may not be executable when
     * it arrives at the server.
     */
    SYNCED
}
