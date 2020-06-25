package gent.timdemey.cards.services.scaleman.img;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import gent.timdemey.cards.services.contract.ImageResource;
import gent.timdemey.cards.services.contract.Resource;
import gent.timdemey.cards.services.scaleman.ScalableResource;

public class ScalableImageResource extends ScalableResource<BufferedImage>
{    
    
    public ScalableImageResource (UUID id, Resource<BufferedImage> resource)
    {
        super(id, resource);   
    }
    
    @Override
    public void rescale(int width, int height)
    {
        // we do not scale fallback images
        if (resource.fallback)
        {
            return;
        }
        
        Dimension dim = new Dimension(width, height);
        
        BufferedImage biScaled = imageCache.get(dim);
        if (biScaled == null)
        {
            BufferedImageScaler scaler = new BufferedImageScaler(resource.bufferedImage, width, height);
            biScaled = scaler.getScaledInstance();
            
            imageCache.put(dim, biScaled);
        }
    }

    @Override
    public BufferedImage get(int width, int height)
    {
        // if this is a fallback image, we don't scale so the 
        // cache will also not be filled with tiled versions.
        // the component using this resource may choose to tile it.
        if (resource.fallback)
        {
            return resource.bufferedImage;
        }
        
        Dimension dim = new Dimension(width, height);

        BufferedImage biScaled = imageCache.get(dim);
        if (biScaled != null)
        {
            return biScaled;
        }
        else
        {
            return resource.bufferedImage;   
        }
    }
}
