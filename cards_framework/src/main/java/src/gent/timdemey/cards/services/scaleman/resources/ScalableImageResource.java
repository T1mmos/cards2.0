package gent.timdemey.cards.services.scaleman.resources;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import gent.timdemey.cards.services.scaleman.ScalableResource;

public class ScalableImageResource extends ScalableResource
{
    private final BufferedImage image_src;
    
    private Map<Dimension, BufferedImage> imageCache;
    
    public ScalableImageResource (UUID id, BufferedImage image)
    {
        super(id);
        
        this.image_src = image;
        this.imageCache = Collections.synchronizedMap(new HashMap<>());        
    }
    
    @Override
    public void rescale(int width, int height)
    {
        Dimension dim = new Dimension(width, height);
        
        BufferedImage biScaled = imageCache.get(dim);
        if (biScaled == null)
        {
            BufferedImageScaler scaler = new BufferedImageScaler(image_src, width, height);
            biScaled = scaler.getScaledInstance();
            
            imageCache.put(dim, biScaled);
        }
    }

    @Override
    public BufferedImage get(int width, int height)
    {
        Dimension dim = new Dimension(width, height);

        BufferedImage biScaled = imageCache.get(dim);
        if (biScaled != null)
        {
            return biScaled;
        }
        else
        {
            return image_src;   
        }
    }
}
