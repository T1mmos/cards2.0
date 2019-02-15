package gent.timdemey.cards.entities;

public interface IContextProviderListener {
    /**
     * Called when a context is set.
     */
    void onContextAdded(ContextType type);
    /**
     * Called when a context is removed.
     */
    void onContextRemoved(ContextType type);
}
