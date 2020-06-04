package gent.timdemey.cards.services.resources;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import javax.imageio.ImageIO;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.services.IResourceRepository;
import gent.timdemey.cards.services.IResourceRepository.ResourceType;
import gent.timdemey.cards.services.IResourceService;

public class ResourceService implements IResourceService
{
    // caches
    private final Map<String, BufferedImage> cache_images = new HashMap<>();
    private final Map<String, Font> cache_fonts = new HashMap<>();
    
    // black-pink checkerboard pattern that can be tiled
    private BufferedImage ERROR_IMAGE;

    private BufferedImage getErrorImage()
    {
        synchronized(this)
        {
            if(ERROR_IMAGE == null)
            {
                BufferedImage img = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = (Graphics2D) img.getGraphics();

                g.setColor(Color.black);
                g.fillRect(0, 0, 80, 80);
                g.setColor(new Color(249, 14, 245));
                g.fillRect(40, 0, 40, 40);
                g.fillRect(0, 40, 40, 40);
                g.drawRect(0, 0, 40, 40);
                g.drawRect(40, 40, 40, 40);
                g.setColor(Color.black);
                g.drawRect(40, 0, 40, 40);
                g.drawRect(0, 40, 40, 40);

                g.dispose();

                ERROR_IMAGE = img;
            }
            
            return ERROR_IMAGE;
        }
    }
    
    @Override
    public BufferedImage getImage(String filename)
    {
        BufferedImage bi = get(cache_images, filename, ResourceType.IMAGE, this::loadImage);
        return bi;
    }

    @Override
    public Font getFont(String filename)
    {
        Font font = get(cache_fonts, filename, ResourceType.FONT, this::loadFont);
        return font;
    }

    @Override
    public void clear()
    {
        cache_images.clear();
        cache_fonts.clear();
    }

    private BufferedImage loadImage(InputStream is, String filename)
    {
        try
        {
            return ImageIO.read(is);
        }
        catch (IOException e)
        {
            Logger.error("Failed to load image %s", filename);
            return getErrorImage();
        }
    }
    
    private Font loadFont(InputStream is, String filename)
    {              
        Font font = null;
        try
        {
            font = Font.createFont(Font.TRUETYPE_FONT, is);
        }
        catch (FontFormatException e)
        {
            Logger.error("Failed to load font %s (FontFormatException)", filename);
        }
        catch (IOException e)
        {
            Logger.error("Failed to load font %s (IOException)", filename);
        } 
        
        if (font == null)
        {
            font = Font.decode("Consolas-12");
        }
   
        return font;
    }
    
    private <T> T get(Map<String, T> cache, String filename, ResourceType resourceType, BiFunction<InputStream, String, T> loadFunc)
    {
        T value = cache.get(filename);
        if (value == null)
        {
            IResourceRepository repo = Services.get(IResourceRepository.class);
            InputStream is = repo.getResourceAsStream(resourceType, filename);
            value = loadFunc.apply(is, filename);
            try
            {
                is.close();
            }
            catch (IOException e)
            {
                Logger.error("Failed to close inputstream for resource %s", filename);
            }
            cache.put(filename, value);
        }
        
        return value;
        
    }
}
