package gent.timdemey.cards.services.contract;

import java.awt.image.BufferedImage;

public class GetImageResourceResponse extends GetResourceResponse
{
    public final BufferedImage bufferedImage;
    
    public GetImageResourceResponse(BufferedImage bufferedImage, boolean fallback)
    {
        super(fallback);
        this.bufferedImage = bufferedImage;
    }
}
