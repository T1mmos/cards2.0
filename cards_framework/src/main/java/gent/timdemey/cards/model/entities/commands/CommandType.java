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
     * it arrives at the server. Some syncable commands may execute partially client-side and
     * finish their logic when the server accepted the commands, e.g. revealing cards 
     * automatically. This logic should be implemented in {@link 
     * CommandBase#postExecute(gent.timdemey.cards.model.state.State) postExecute}
     * which is executed at acceptance time. The unexecute logic should only undo the logic
     * implemented by {@link CommandBase#preExecute(gent.timdemey.cards.model.state.State) 
     * preExecute}.
     */
    SYNCED
}
