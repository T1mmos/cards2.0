package gent.timdemey.cards.model.entities.commands.contract;

public enum ExecutionState
{
    Yes,
    
    /**
     * Can execute, but no attempt should be made to execute and unexecute as the action is irreversible, e.g.
     * an action that persists data.
     */
    YesPerm,
    No,
    Error
}
