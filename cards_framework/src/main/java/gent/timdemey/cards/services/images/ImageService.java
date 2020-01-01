package gent.timdemey.cards.services.images;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.services.IImageService;
import gent.timdemey.cards.services.IResourceManager;

public class ImageService implements IImageService {

    @Override
    public BufferedImage read(String filename) {
        Services.get(ILogManager.class).log("Read image: " + filename);
        IResourceManager resMan = Services.get(IResourceManager.class);
        InputStream is = resMan.getResourceAsStream(filename);
        try {
            BufferedImage image = ImageIO.read(is);
            return image;
        } catch (IOException e) {
            return createErrorImage();
        }
    }
    
    private BufferedImage createErrorImage()
    {
        BufferedImage img = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.setColor(Color.white);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        
        g.setColor(Color.red);
        g.drawLine(0, 0, img.getWidth(), img.getHeight());
        g.drawLine(0, img.getHeight(), img.getWidth(), 0);
        
        g.dispose();
        
        return img;
    }
}
