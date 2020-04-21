package gent.timdemey.cards.model.entities.commands;

public enum CommandExecutionState
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
     * The command could not be reexecuted after an insertion. This indicates that another command was either inserted
     * in the command chain, and now this command is now no longer executable. This does however not mean
     * that the command will not be accepted in the future; it is possible, yet unlikely, that another command will get
     * inserted that will. 
     * <p>An example: observe the case where 2 or more (remote) commands of a different player are executed server side and only 
     * then the local command is processed. In the meantime we received an insertion for remote command 1, where we detect that
     * the local command is no longer executable so it's put in quarantine. Then we receive the insertion for remote command 2,
     * and now we detect that the local command is again executable. The server will also see that the local commmand 
     * is executable, will accept it, and thus we will receive an accept for our local command, even after it was put in 
     * quarantine.
     * <p>It most be noted that the chance of getting accepted after being put in quarantine will be rather low for most games.
     * E.g. for Solitaire Showdown, both players want to put down an ace on the same stack. The remote player wins and therefore
     * you first get an insert to process, and as such you detect that your ace can't be put down anymore. Your command goes in
     * quarantine. But, in Solitaire Showdown, no player can pick up cards from the common stacks. Therefore, your ace will never
     * be able to get put on that stack during the entire game. So, in the end, the only outcome is that 
     * your 'put ace here' command will be rejected.
     * <p>
     * Because the chance of getting accepted after quarantine is estimated to be low, the client will not put effort in 
     * trying to reexecute the command after multiple insertions have occured. 
     */
    Quarantined,
    
    /**
     * Unexecuted while awaiting confirmation from the server. This is a temporary state. When redoing the command, 
     * two things can happen: it can execute successfully or it can't. In the former case we'll transition to AwaitingConfirmation,
     * in the latter case we'll go to Quarantined.
     */
    UnexecutedAwaitingConfirmation,
    
    /**
     * Executed, but awaiting confirmation from the server. A command in this state
     * cannot be undone by the user, but only by the server that doesn't accept the command. It may 
     * also be temporarily undone when other commands need to be injected or rejected in the history,
     * in which case it will transition to UnexecutedAwaitingConfirmation.
     */
    AwaitingConfirmation,

    /**
     * Executed and accepted by the server. This is a final state.
     */
    Accepted,
    
    /**
     * Unexecuted because the server didn't accept the command. This is a final state.
     */
    Rejected,
}
