package gent.timdemey.cards.services.scaleman;

import java.awt.Dimension;
import java.util.Map;
import java.util.UUID;

import gent.timdemey.cards.services.contract.Resource;

/**
 * An abstract IScalableResource that implements caching for quick access to the 
 * scaled instances of the original raw resource.
 * @author Tim
 *
 * @param <T>
 */
public abstract class ScalableResource<R> implements IScalableResource<R>
{
    public final UUID id;
    
    public final Resource<R> resource;

    private Map<Dimension, R> cache;
    
    public ScalableResource (UUID id, Resource<R> resource)
    {
        this.id = id;
        this.resource = resource;
    }
    
    @Override
    public UUID getId()
    {
        return id;
    }
    
    public final R get(int width, int height)
    {
        Dimension dim = new Dimension(width, height);
        R rawRes = cache.get(dim);
        if (rawRes == null)
        {
            String msg = String.format("The resource in dimensions %x,%y is not found", width, height);
            throw new IllegalStateException(msg);
        }
        return rawRes;
    }
}
