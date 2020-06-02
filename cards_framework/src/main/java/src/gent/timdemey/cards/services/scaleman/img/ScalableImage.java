package gent.timdemey.cards.services.scaleman.img;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.UUID;

import javax.swing.JComponent;

import gent.timdemey.cards.services.scaleman.ScalableComponent;

public class ScalableImage extends ScalableComponent
{
    private String file = null;
    private BufferedImage image = null;
    
 // black-pink checkerboard pattern that can be tiled
    private static BufferedImage ERROR_IMAGE;

    private static BufferedImage getErrorImage()
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

    
    public ScalableImage(UUID modelId)
    {
        super(modelId);        
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

    public void setImage(BufferedImage image, String file)
    {
        this.file = file;
        this.image = image;
    }

    public void draw(Graphics2D g2)
    {
        JComponent comp = getComponent();
        int width = comp.getWidth();
        int height = getComponent().getWidth();
        
        if(image != null)
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
        else
        {
            BufferedImage errorImg = getErrorImage();
            if(errorImg != null)
            {
                int tileWidth = errorImg.getWidth();
                int tileHeight = errorImg.getHeight();
                for (int y = 0; y < height; y += tileHeight)
                {
                    for (int x = 0; x < width; x += tileWidth)
                    {
                        g2.drawImage(errorImg, x, y, comp);
                    }
                }
            }
        }
    }
}
