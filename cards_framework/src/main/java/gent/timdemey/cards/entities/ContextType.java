package gent.timdemey.cards.entities;

public enum ContextType {
    /**
     * Context is the event dispatching thread.
     */
    UI,
    /**
     * Context on a client where all async processing takes place: incoming commands from UI context and server context. In this 
     * context, we ensure that clients are in sync with the server by rolling back commands that are not allowed on the server.
     */
    Client,
    /**
     * Context on a server. 
     */
    Server;
}
