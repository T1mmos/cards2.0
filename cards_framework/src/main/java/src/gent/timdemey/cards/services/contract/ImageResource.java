package gent.timdemey.cards.services.contract;

import java.awt.image.BufferedImage;

public class ImageResource extends Resource
{
    public final BufferedImage bufferedImage;
    
    public ImageResource(BufferedImage bufferedImage, String filename, boolean fallback)
    {
        super(filename, fallback);
        this.bufferedImage = bufferedImage;
    }
}
