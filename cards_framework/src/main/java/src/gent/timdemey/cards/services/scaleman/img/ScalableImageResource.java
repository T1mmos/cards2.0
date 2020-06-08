package gent.timdemey.cards.services.scaleman.img;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import gent.timdemey.cards.services.scaleman.ScalableResource;

public class ScalableImageResource extends ScalableResource
{
    private final BufferedImage image_src;
    
    private Map<Dimension, BufferedImage> imageCache;
    
    public ScalableImageResource (String id, BufferedImage image, boolean fallback)
    {
        super(id, fallback);
        
        this.image_src = image;
        this.imageCache = Collections.synchronizedMap(new HashMap<>());        
    }
    
    @Override
    public void rescale(int width, int height)
    {
        // we do not scaled fallback images
        if (fallback)
        {
            return;
        }
        
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
        // if this is a fallback image, we don't scale so the 
        // cache will also not be filled with tiled versions.
        // the component using this resource may choose to tile it.
        if (fallback)
        {
            return image_src;
        }
        
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
