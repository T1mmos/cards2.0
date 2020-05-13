package gent.timdemey.cards.services.context;

public interface IContextListener
{
    void onContextInitialized (ContextType type);
    void onContextDropped (ContextType type);
}
