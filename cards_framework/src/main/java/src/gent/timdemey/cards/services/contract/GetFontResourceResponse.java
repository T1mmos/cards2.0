package gent.timdemey.cards.services.contract;

import java.awt.Font;

public class GetFontResourceResponse extends GetResourceResponse
{
    public final Font font;
    
    public GetFontResourceResponse(Font font, boolean fallback)
    {
        super(fallback);
        this.font = font;
    }
}
