package gent.timdemey.cards.services.scaleman;

import java.util.UUID;

import gent.timdemey.cards.services.contract.Resource;

public abstract class ScalableResource<T extends Resource<?>> implements IScalableResource
{
    public final UUID id;
    
    public final T resource;
    
    public ScalableResource (UUID id, T resource)
    {
        this.id = id;
        this.resource = resource;
    }
    
    @Override
    public UUID getId()
    {
        return id;
    }
}
