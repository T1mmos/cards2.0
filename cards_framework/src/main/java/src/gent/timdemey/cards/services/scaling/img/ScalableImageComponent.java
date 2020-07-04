package gent.timdemey.cards.services.scaling.img;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.services.scaling.IScalableResource;
import gent.timdemey.cards.services.scaling.ScalableComponent;

public abstract class ScalableImageComponent extends ScalableComponent<BufferedImage>
{
    private final List<ScalableImageResource> imgResources;    
    private IScalableResource<BufferedImage> currentScaledResource;
    
    public ScalableImageComponent(UUID id, ScalableImageResource ... resources)
    {
        super(id);  

        this.imgResources = Arrays.asList(resources);
        this.currentScaledResource = null;
    }

    @Override
    protected List<String> getDebugStrings()
    {
        List<String> allStrings = new ArrayList<>(super.getDebugStrings());
        allStrings.add(String.format("file=%s", currentScaledResource.getResource().filename));
        return allStrings;
    }
        
    /**
     * Swap the image shown.
     * @param resourceId
     */
    protected final void setScalableImageResource(UUID resourceId)
    {
        IScalableResource<BufferedImage> found = null;
        for (IScalableResource<BufferedImage> resource : imgResources)
        {
            if (resource.getId().equals(resourceId))
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
        
        currentScaledResource = found;
    }

    @Override
    protected final void draw(Graphics2D g2)
    {
        Dimension dim = getBounds().getSize();
        
        BufferedImage bi = currentScaledResource.get(dim);
        
        if (!currentScaledResource.getResource().fallback)
        {
            drawScaled(g2, bi, dim);
        }
        else
        {
            drawTiled(g2, bi, dim);
        }
    }
    
    /**
     * Draw the image rescaled.
     * @param g2
     * @param image
     * @param width
     * @param height
     */
    private void drawScaled(Graphics2D g2, BufferedImage image, Dimension dim)
    {
        int width = dim.width;
        int height = dim.height;
        Graphics2D g3 = (Graphics2D) g2.create();
        if(isMirror())
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

    private void drawTiled(Graphics2D g2, BufferedImage image, Dimension dim)
    {
        Graphics2D g3 = (Graphics2D) g2.create();
        
        int tileWidth = image.getWidth();
        int tileHeight = image.getHeight();
        for (int y = 0; y < dim.height; y += tileHeight)
        {
            for (int x = 0; x < dim.width; x += tileWidth)
            {
                g3.drawImage(image, x, y, null);
            }
        }
        
        g3.dispose();
    }
}
