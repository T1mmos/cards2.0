package gent.timdemey.cards.services.scaleman;

import java.util.UUID;

public abstract class ScalableResource implements IScalableResource
{
    public final UUID id;
    
    public ScalableResource (UUID id)
    {
        this.id = id;
    }
    
    @Override
    public UUID getId()
    {
        return id;
    }
}
