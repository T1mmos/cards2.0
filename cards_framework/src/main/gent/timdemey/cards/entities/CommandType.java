package gent.timdemey.cards.entities;

/**
 * Enumerates command types. Priority of processing is based on the command type. 
 * The enum instances must be listed from low priority to high.
 * @author Timmos
 *
 */
public enum CommandType {
    /**
     * Normal gameplay command, that changes the game state. Commands of this type are
     * recorded in the gamestate history, which can be rolled back (undo/redo) when
     * needed (either an actual user-invoked undo in case of single player or a
     * forced rollback triggered by the server in a multiplayer game).
     */
    Gameplay,
    /**
     * Correction command, to correct the client state according to the server state. This
     * should be the highest prioritized command type at client side because subsequent,
     * user-invoked commands may be based on invalid state, so the invalid command must
     * be fixed first in order to minimize the number of command to roll back.
     */
    Correction,
    /**
     * Server-only commands that should never be sent to or from a client.
     */
    ServerOnly,
    
    /**
     * Meta commands, e.g. joining a game, server discovery, ...
     */
    Meta
}
