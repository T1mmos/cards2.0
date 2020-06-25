package gent.timdemey.cards.services.contract;

import java.awt.image.BufferedImage;

public class ImageResource extends Resource<BufferedImage>
{
    public ImageResource(String filename, boolean fallback, BufferedImage bi)
    {
        super(filename, fallback, bi);
    }
}
