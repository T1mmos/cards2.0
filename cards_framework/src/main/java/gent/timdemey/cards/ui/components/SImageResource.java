package gent.timdemey.cards.ui.components;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.UUID;

import gent.timdemey.cards.services.contract.res.Resource;

public final class SImageResource extends SResource<BufferedImage>
{
    public SImageResource(UUID id, Resource<BufferedImage> resource)
    {
        super(id, resource);
    }

    @Override
    public boolean canRescale()
    {
        // we do not scale fallback images
        if (resource.fallback)
        {
            return false;
        }

        return true;
    }

    @Override
    protected BufferedImage rescaleImpl(Dimension dim)
    {
        BufferedImageScaler scaler = new BufferedImageScaler(resource.raw, dim.width, dim.height);
        BufferedImage biScaled = scaler.getScaledInstance();
        return biScaled;
    }
}
