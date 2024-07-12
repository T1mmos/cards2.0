package gent.timdemey.cards.services.context;

public enum ContextType
{
    /**
     * Context is the event dispatching thread.
     */
    UI,
    /**
     * Context on a server.
     */
    Server;
}
