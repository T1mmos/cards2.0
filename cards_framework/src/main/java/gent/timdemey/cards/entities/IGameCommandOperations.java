package gent.timdemey.cards.entities;

/**
 * Same as normal game operations, but the parameters are in Command form, a non-public class. This is 
 * used to check incoming commands (in the non-UI layer) for validity - e.g. a command coming from the 
 * UI, or a command coming from a client which must be validated server side. The methods do not belong
 * in IGameOperations as the parameters are not visible outside the package, but the method implementations
 * can / should delegate to the appropriate methods from IGameOperations (to keep the logic central).
 * <p>As the commands are ready to be processed as-is by the CommandProcessor, only the "can" methods 
 * should be implemented.
 * @author Timmos
 */
interface IGameCommandOperations {

    boolean canStartGame (C_StartGame command);
    boolean canStopGame(C_StopGame command);
    boolean canMove (C_Move command);
}
