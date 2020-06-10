package gent.timdemey.cards.services.contract;

import java.awt.Font;

public class FontResource extends Resource
{
    public final Font font;
    
    public FontResource(Font font, String filename, boolean fallback)
    {
        super(filename, fallback);
        this.font = font;
    }
}
