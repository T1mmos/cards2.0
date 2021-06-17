package gent.timdemey.cards.ui.components.drawers;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import javax.swing.JPanel;

import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.services.contract.GetResourceResponse;
import gent.timdemey.cards.ui.components.ISResource;
import gent.timdemey.cards.ui.components.SImageResource;

public class ScaledImageDrawer extends DrawerBase<JPanel>
{
    private final List<SImageResource> imgResources;
    private ISResource<BufferedImage> currentScaledResource;

    public ScaledImageDrawer (SImageResource... resources)
    {
        this.imgResources = Arrays.asList(resources);
        this.currentScaledResource = null;
    }
    
    /**
     * Swap the image shown.
     * 
     * @param resourceId
     */
    public final void setScalableImageResource(UUID resourceId)
    {
        ISResource<BufferedImage> found = null;
        for (ISResource<BufferedImage> resource : imgResources)
        {
            if(resource.getId().equals(resourceId))
            {
                found = resource;
                break;
            }
        }

        if(found == null)
        {
            Logger.error("Resource with id=%s not found", resourceId);
            return;
        }

        currentScaledResource = found;
    }
    
    @Override
    public final void drawForeground(Graphics2D g2, Consumer<Graphics> superPaintComponent)
    {
        Graphics2D g = (Graphics2D) g2.create();
        Dimension currDim = comp.getSize();

        GetResourceResponse<BufferedImage> resp = currentScaledResource.get(currDim);
        BufferedImage bi = resp.resource;
        if(!currentScaledResource.getResource().fallback)
        {
            drawScaled(g, bi, currDim);
        }
        else
        {
            drawTiled(g, bi, currDim);
        }
    }

    /**
     * Draw the image rescaled.
     * 
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
    
    @Override
    public List<String> getDebugStrings()
    {
        List<String> allStrings = new ArrayList<>(super.getDebugStrings());
        allStrings.add(String.format("file=%s", currentScaledResource.getResource().filename));
        return allStrings;
    }
}
