package gent.timdemey.cards.services.contract.res;

import java.awt.Font;

public class FontResource extends Resource<Font>
{    
    public FontResource(String filename, boolean fallback, Font font)
    {
        super(filename, fallback, font);
    }
}
