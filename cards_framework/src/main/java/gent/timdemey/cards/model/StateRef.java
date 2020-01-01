package gent.timdemey.cards.model;

public final class StateRef<X>
{
    public final String id;
    private X x;
    
    StateRef(String id, X x)
    {
        this.id = id;
        this.x = x;
    }
    
    void set(X x)
    {
        this.x = x;
    }
    
    X get()
    {
        return x;
    }
}
