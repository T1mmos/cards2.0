package gent.timdemey.cards.services.scaleman;

import java.awt.Dimension;
import java.util.Collections;
import java.util.HashMap;
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

    protected abstract R rescaleImpl(int width, int height);

    @Override
    public final Resource<R> getResource()
    {
        return resource;
    }

    @Override
    public final void rescale(int width, int height)
    {
        if (!canRescale())
        {
            return;
        }

        Dimension dim = new Dimension(width, height);
        R scaled = cache.get(dim);
        if (scaled == null)
        {
            scaled = rescaleImpl(width, height);
            cache.put(dim, scaled);
        }
    }

    public final R get(int width, int height)
    {
        Dimension dim = new Dimension(width, height);
        R rawRes = cache.get(dim);
        if (rawRes == null)
        {
            rawRes = resource.raw;
        }
        return rawRes;
    }
}
