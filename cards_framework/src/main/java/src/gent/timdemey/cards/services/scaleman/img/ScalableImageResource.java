package gent.timdemey.cards.services.scaleman.img;

import java.awt.image.BufferedImage;
import java.util.UUID;

import gent.timdemey.cards.services.contract.Resource;
import gent.timdemey.cards.services.scaleman.ScalableResource;

public class ScalableImageResource extends ScalableResource<BufferedImage>
{

    public ScalableImageResource(UUID id, Resource<BufferedImage> resource)
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
    protected BufferedImage rescaleImpl(int width, int height)
    {
        BufferedImageScaler scaler = new BufferedImageScaler(resource.raw, width, height);
        BufferedImage biScaled = scaler.getScaledInstance();
        return biScaled;
    }
}
