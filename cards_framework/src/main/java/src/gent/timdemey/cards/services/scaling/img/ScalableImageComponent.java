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
import gent.timdemey.cards.services.contract.GetResourceResponse;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.services.scaling.IScalableResource;
import gent.timdemey.cards.services.scaling.ScalableComponent;

public final class ScalableImageComponent extends ScalableComponent
{
    private final List<ScalableImageResource> imgResources;    
    private IScalableResource<BufferedImage> currentScaledResource;
    
    public ScalableImageComponent(UUID id, ComponentType compType, ScalableImageResource ... resources)
    {
        super(id, compType);  

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
    public final void setScalableImageResource(UUID resourceId)
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
        Dimension currDim = getCoords().getSize();
        
        GetResourceResponse<BufferedImage> resp = currentScaledResource.get(currDim);
        BufferedImage bi = resp.resource;
        if (!currentScaledResource.getResource().fallback)
        { 
            drawScaled(g2, bi, currDim);           
        }
        else
        {
            drawTiled(g2, bi, currDim);
        }
    }
           
    /**
     * Draw the image rescaled.
     * @param g2
     * @param image
     * @param width
     * @param height
     */
    private void drawScaled(Graphics2D g2, BufferedImage image, Dimension currDim)
    {
        int currWidth = currDim.width;
        int currHeight = currDim.height;
        Graphics2D g3 = (Graphics2D) g2.create();
        if(isMirror())
        {
            g3.scale(-1.0, -1.0);
            g3.translate(-currWidth, -currHeight);
        }

        int imgW = image.getWidth();
        int imgH = image.getHeight();

        if(imgW == currWidth && imgH == currHeight)
        {
            // best image quality
            g3.drawImage(image, 0, 0, null);
        }
        else
        {
            // quick rescale until bufferedimage is updated with high quality
            g3.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
            double sx = 1.0 * currWidth / imgW;
            double sy = 1.0 * currHeight / imgH;
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
