package gent.timdemey.cards.services.resources;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.IFontService;
import gent.timdemey.cards.services.IResourceRepository;
import gent.timdemey.cards.services.IResourceRepository.ResourceType;

public class FontService implements IFontService
{

    @Override
    public Font getFont(String name)
    {
        IResourceRepository resRepo = Services.get(IResourceRepository.class);
        
        Font f2;
        try (InputStream is = resRepo.getResourceAsStream(ResourceType.FONTS, name))
        {            
            Font f = Font.createFont(Font.TRUETYPE_FONT, is);
            GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
            genv.registerFont(f);
            f2 = f.deriveFont(12f);
        }
        catch (Exception e)
        {
            f2 = Font.decode("Consolas-12");
        }
        return f2;
    }

}
