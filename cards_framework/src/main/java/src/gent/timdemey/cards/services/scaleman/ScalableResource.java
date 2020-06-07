package gent.timdemey.cards.services.scaleman;

public abstract class ScalableResource implements IScalableResource
{
    public final String id;
    
    public ScalableResource (String id)
    {
        this.id = id;
    }
    
    @Override
    public String getId()
    {
        return id;
    }
}
