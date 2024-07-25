package gent.timdemey.cards.services.resources;

import gent.timdemey.cards.localization.Loc;
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


import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.services.contract.res.AudioResource;
import gent.timdemey.cards.services.contract.res.FontResource;
import gent.timdemey.cards.services.contract.res.ImageResource;
import gent.timdemey.cards.services.contract.res.ResourceType;
import gent.timdemey.cards.services.interfaces.IResourceCacheService;
import gent.timdemey.cards.services.interfaces.IResourceRepository;
import java.io.ByteArrayInputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class ResourceCacheService implements IResourceCacheService
{
    // caches
    private final Map<String, ImageResource> cache_images = new HashMap<>();
    private final Map<String, FontResource> cache_fonts = new HashMap<>();
    private final Map<String, AudioResource> cache_audio = new HashMap<>();
    
    // black-pink checkerboard pattern that can be tiled
    private BufferedImage ERROR_IMAGE;
    
    private final IResourceRepository _ResourceRepository;
    private final Logger _Logger;

    public ResourceCacheService (
        IResourceRepository resourceRepository,
        Logger logger
    )
    {
        this._ResourceRepository = resourceRepository;
        this._Logger = logger;
    }
    
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
    public ImageResource getImage(String filename)
    {
        ImageResource resp_cached = get(cache_images, filename, ResourceType.IMAGE, this::loadImage);
        return resp_cached;
    }

    @Override
    public FontResource getFont(String filename)
    {
        FontResource resp_cached = get(cache_fonts, filename, ResourceType.FONT, this::loadFont);
        return resp_cached;
    }
    
    @Override
    public AudioResource getAudio(String filename)
    {
        AudioResource resp_cached = get(cache_audio, filename, ResourceType.SOUND, this::loadSound);
        return resp_cached;
    }

    @Override
    public void clear()
    {
        cache_images.clear();
        cache_fonts.clear();
    }

    private ImageResource loadImage(InputStream is, String filename)
    {
        try
        {
            BufferedImage bi = ImageIO.read(is);
            return new ImageResource(filename, false, bi);
        }
        catch (IOException e)
        {
            _Logger.error("Failed to load image %s", filename);
            BufferedImage bi = getErrorImage();
            return new ImageResource(filename, true, bi);
        }
    }
    
    private FontResource loadFont(InputStream is, String filename)
    {              
        Font font = null;
        try
        {
            font = Font.createFont(Font.TRUETYPE_FONT, is);
        }
        catch (FontFormatException e)
        {
            _Logger.error("Failed to load font %s (FontFormatException)", filename);
        }
        catch (IOException e)
        {
            _Logger.error("Failed to load font %s (IOException)", filename);
        } 
        
        boolean fallback = false;
        if (font == null)
        {
            font = Font.decode("Consolas-12");
            fallback = true;
        }
   
        return new FontResource(filename, fallback, font);
    }
    
    private AudioResource loadSound(InputStream is, String filename)
    {           
        try
        {
            byte[] arr = new byte[is.available()];
            is.read(arr);
                                     
            @SuppressWarnings("resource")
            Clip clip = AudioSystem.getClip();

            ByteArrayInputStream bis = new ByteArrayInputStream(arr);
            @SuppressWarnings("resource")
            AudioInputStream ais = AudioSystem.getAudioInputStream(bis);
            clip.open(ais);
            
            return new AudioResource(filename, false, clip);
        }
        catch (Exception e)
        {
            _Logger.error(e);
            return null;
        }
    }
    
    private <T> T get(Map<String, T> cache, String filename, ResourceType resourceType, BiFunction<InputStream, String, T> loadFunc)
    {
        T value = cache.get(filename);
        if (value == null)
        {
            InputStream is = _ResourceRepository.getResourceAsStream(resourceType, filename);
            value = loadFunc.apply(is, filename);
            try
            {
                is.close();
            }
            catch (IOException e)
            {
                _Logger.error("Failed to close inputstream for resource %s", filename);
            }
            cache.put(filename, value);
        }
        
        return value;
        
    }
}
