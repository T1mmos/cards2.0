package gent.timdemey.cards.model.entities.game;

public enum GameState
{
    /**
     * The client is not connected to any server. This state is invalid server-side.
     */
    NotConnected,
    
    /**
     * Server-side the lobby is created and client-side, the client has 
     * entered a lobby.
     */
    Lobby,
    
    /**
     * The game is started.
     */
    Started,
    
    /**
     * The game is paused.
     */
    Paused,
    
    /**
     * The game has ended.
     */
    Ended
}
