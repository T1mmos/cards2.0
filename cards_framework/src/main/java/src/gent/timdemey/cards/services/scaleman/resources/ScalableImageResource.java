package gent.timdemey.cards.services.scaleman.resources;

import java.awt.image.BufferedImage;
import java.util.UUID;

import gent.timdemey.cards.services.scaleman.ScalableResource;

public class ScalableImageResource extends ScalableResource
{
    private final BufferedImage image_src;
    
    private BufferedImage image_scaled_current;
    private BufferedImage image_scaled_working;
    
    public ScalableImageResource (UUID id, BufferedImage image)
    {
        super(id);
        this.image_src = image;
        this.image_scaled_current = null;
        this.image_scaled_working = null;
    }
    
    @Override
    public void rescale(int width, int height)
    {
        // call the rescaler...
        // this.image_scaled = ...
    }

    @Override
    public void publish()
    {
        image_scaled_current = this.image_scaled_working;
        image_scaled_working = null;
    }

    @Override
    public BufferedImage get()
    {
        return image_scaled_current;
    }
}
