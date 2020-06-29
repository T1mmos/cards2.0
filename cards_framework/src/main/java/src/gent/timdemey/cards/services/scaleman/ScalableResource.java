package gent.timdemey.cards.services.scaleman;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import gent.timdemey.cards.services.contract.Resource;

/**
 * An abstract IScalableResource that implements caching for quick access to the
 * scaled instances of the original raw resource.
 * 
 * @author Tim
 *
 * @param <T>
 */
public abstract class ScalableResource<R> implements IScalableResource<R>
{
    public final UUID id;
    public final Resource<R> resource;
    private final Map<Dimension, R> cache;

    public ScalableResource(UUID id, Resource<R> resource)
    {
        this.id = id;
        this.resource = resource;
        this.cache = Collections.synchronizedMap(new HashMap<>());
    }

    @Override
    public UUID getId()
    {
        return id;
    }

    protected abstract boolean canRescale();

    protected abstract R rescaleImpl(Dimension target);

    @Override
    public final Resource<R> getResource()
    {
        return resource;
    }

    @Override
    public final void rescale(Dimension target)
    {
        if (!canRescale())
        {
            return;
        }
        
        Dimension canonical = canonical(target);

        R scaled = cache.get(canonical);
        if (scaled == null)
        {
            scaled = rescaleImpl(canonical);
            cache.put(canonical, scaled);
        }
    }
    
    /**
     * Projects the dimension into a canonical dimension. For example,
     * if the height is irrelevant for this resource, one could return 
     * a dimension with the same width as in the given dimension, 
     * but with the height set to zero.
     * <p>By default, the given dimension is returned.
     * @param dim
     * @return
     */
    protected Dimension canonical(Dimension dim)
    {
        return dim;
    }

    @Override
    public final R get(Dimension dim)
    {
        Dimension canonical = canonical(dim);
        R rawRes = cache.get(canonical);
        if (rawRes == null)
        {
            rawRes = resource.raw;
        }
        return rawRes;
    }

    @Override
    public List<Dimension> getDimensions()
    {
        return new ArrayList<>(cache.keySet());
    }
}
