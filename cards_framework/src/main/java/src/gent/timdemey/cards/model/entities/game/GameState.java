package gent.timdemey.cards.model.entities.game;

public enum GameState
{
    /**
     * The client is not connected to any server. This state is invalid server-side.
     */
    Disconnected,
    
    /**
     * TCP connection is set up but additional handshaking (application level) needs to complete. The TCP 
     * connection may be closed server side because e.g. the lobby is full. The server
     * will first send a command in all cases so the client can be notified, and in the
     * case of rejecting the connection, the client can expect the TCP connection to be
     * closed.
     * This state is invalid server-side.
     */
    Connected,
    
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
