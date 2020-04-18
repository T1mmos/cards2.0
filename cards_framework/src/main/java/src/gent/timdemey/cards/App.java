package gent.timdemey.cards;

public class App
{
    private static Services services;
    
    private App ()
    {
    }
    
    public static void init(Services services)
    {
        if (services == null)
        {
            throw new NullPointerException("services cannot be null");
        }
        if (App.services != null)
        {
            throw new IllegalStateException("Services already set. You can only set this once per application lifetime.");
        }
        
        App.services = services;
    }
    
    public static Services getServices()
    {
        return services;
    }
}
