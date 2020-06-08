package gent.timdemey.cards.services.scaleman.img;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;

import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.services.scaleman.ScalableComponent;

public class ScalableImageComponent extends ScalableComponent
{
    private String file = null;
    private final List<ScalableImageResource> imageResources;
    private ScalableImageResource currentImageResource;
    
    public ScalableImageComponent(String id, ScalableImageResource ... imageResources)
    {
        super(id);  
        this.imageResources = Arrays.asList(imageResources);
        this.currentImageResource = null;
    }

    @Override
    protected JComponent createComponent()
    {
        return new JScalableImage(this);
    }

    @Override
    protected String[] addDebugStrings()
    {
        return new String[] { String.format("file=%s", file) };
    }
    
    /**
     * Swap the image shown.
     * @param resourceId
     */
    public void setScalableImageResource(String resourceId)
    {
        ScalableImageResource found = null;
        for (ScalableImageResource resource : imageResources)
        {
            if (resource.id.equals(resourceId))
            {
                found = resource;
                break;
            }
        }
        
        if (found == null)
        {
            Logger.error("Resource with id=%s not found", resourceId);
            return;            
        }
        
        currentImageResource = found;
        repaint();
    }

    public void draw(Graphics2D g2)
    {
        int width = getBounds().width;
        int height = getBounds().height;
        
        BufferedImage bi = currentImageResource.get(width, height);
        
        if (!currentImageResource.fallback)
        {
            drawScaled(g2, bi, width, height);
        }
        else
        {
            drawTiled(g2, bi, width, height);
        }
    }
    
    /**
     * Draw the image rescaled.
     * @param g2
     * @param image
     * @param width
     * @param height
     */
    private void drawScaled(Graphics2D g2, BufferedImage image, int width, int height)
    {
        Graphics2D g3 = (Graphics2D) g2.create();
        if(getMirror())
        {
            g3.scale(-1.0, -1.0);
            g3.translate(-width, -height);
        }

        int imgW = image.getWidth();
        int imgH = image.getHeight();

        if(imgW == width && imgH == height)
        {
            // best image quality
            g3.drawImage(image, 0, 0, null);
        }
        else
        {
            // quick rescale until bufferedimage is updated with high quality
            g3.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            double sx = 1.0 * width / imgW;
            double sy = 1.0 * height / imgH;
            g3.scale(sx, sy);
            g3.drawImage(image, 0, 0, null);
        }
        
        g3.dispose();
    }

    private void drawTiled(Graphics2D g2, BufferedImage image, int width, int height)
    {
        Graphics2D g3 = (Graphics2D) g2.create();
        
        int tileWidth = image.getWidth();
        int tileHeight = image.getHeight();
        for (int y = 0; y < height; y += tileHeight)
        {
            for (int x = 0; x < width; x += tileWidth)
            {
                g3.drawImage(image, x, y, null);
            }
        }
        
        g3.dispose();
    }
   
    @Override
    public List<ScalableImageResource> getResources()
    {
        return imageResources;
    }

    @Override
    public void update()
    {
        // TODO Auto-generated method stub
        
    }
}
