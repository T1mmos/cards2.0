package gent.timdemey.cards.ui.components.ext;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.contract.Coords;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.ui.components.IHasAlpha;
import gent.timdemey.cards.ui.components.IHasTile;
import gent.timdemey.cards.utils.ColorUtils;
import gent.timdemey.cards.utils.DebugDrawDefines;

public abstract class DrawerBase implements IDrawer
{
    private boolean mirror = false;
    private float alpha = 1.0f;    
    private float alphaBg = 1.0f;  
    
    private Color colorBg = Color.BLACK;
    private BufferedImage tile;    
    
    public void setMirror(boolean mirror)
    {
        this.mirror = mirror;
    }
    
    public boolean isMirror()
    {
        return mirror;
    }

    @Override
    public void setForegroundAlpha(float alpha)
    {
        this.alpha = alpha;
    }
    
    @Override
    public float getForegroundAlpha()
    {
        return alpha;
    }
    
    @Override
    public void setBackgroundAlpha(float alpha)
    {
        this.alphaBg = alpha;
    }

    @Override 
    public float getBackgroundAlpha()
    {
        return alphaBg;
    }
    
    /**
     * Draws everything: background, content, borders, debug stuff, ...
     * @param g
     */
    @Override
    public final void draw(Graphics2D g, JComponent comp)
    {
        // draw mirrored
        {
            Graphics2D g2 = (Graphics2D) g.create();
            if(isMirror())
            {
                g2.scale(-1.0, -1.0);
                g2.translate(-comp.getWidth(), -comp.getHeight());
            }
            
            drawBackground(g);        
            drawContent(g);
        }
        
        // draw non-mirrored
        {
            drawDebug(g);
        }        
    }
    
    private final void drawContent(Graphics2D g)
    {
        Graphics2D g2 = (Graphics2D) g.create();
        
        if (this instanceof IHasAlpha)
        {
            IHasAlpha sTransp = (IHasAlpha) this;            
            float alpha = sTransp.getForegroundAlpha();
            Composite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            g2.setComposite(composite);
        }
        drawForeground(g2);
        
        g2.dispose();
    }
    
    private final void drawBackground(Graphics2D g)
    {
        Graphics2D g2 = (Graphics2D) g.create();
        
        // support for background alpha
        if (this instanceof IHasAlpha)
        {
            IHasAlpha hasTrans = (IHasAlpha) this;
            applyAlpha(g, hasTrans.getBackgroundAlpha());
        }
        
        Rectangle bounds = g2.getClipBounds();
        int x = bounds.x;
        int y = bounds.y;
        int w = bounds.width;
        int h = bounds.height;
        
        // draw tiles i.o. background fill if we have a tile
        BufferedImage tile = null;
        if (this instanceof IHasTile)
        {
            IHasTile hasTile = (IHasTile) this;
            tile = hasTile.getTile();
        }
        
        if (tile != null)
        {
            g2.clearRect(x, y, w, h);
            int tileWidth = tile.getWidth();
            int tileHeight = tile.getHeight();
            for (int j = y; j < y+h; j += tileHeight)
            {
                for (int i = x; i < x+w; i += tileWidth)
                {
                    g2.drawImage(tile, i, j, null);
                }
            }
        }
        else 
        {       
            Color bgColor;
            if (this instanceof IHasAlpha)
            {
                IHasAlpha hasTrans = (IHasAlpha) this;
                bgColor = ColorUtils.transparent(getBackground(), hasTrans.getBackgroundAlpha());
            }
            else
            {
                bgColor = getBackground();
            }
            g2.setColor(bgColor);
            g2.fillRect(x, y, w - 1, h - 1);
        }
        
        g2.dispose();
    }
    
    protected abstract void drawForeground(Graphics2D g2);

    public final void drawDebug(Graphics2D g2)
    {
        if(!Services.get(IFrameService.class).getDrawDebug())
        {
            return;
        }
        Coords.Absolute coords = getCoords();
        // dimmed overlay color, to make (white) debug text readable
        {
            Graphics2D g = (Graphics2D) g2.create();
            g.setColor(DebugDrawDefines.COLOR_DIMMED_COMPONENT_BACKGROUND);
            g.fillRect(0, 0, coords.w, coords.h);
        }

        drawDebugBoundaries(g2);

        List<String> debugStrings = getDebugStrings();
        drawDebugStrings(g2, debugStrings);
    }

    protected List<String> getDebugStrings()
    {
        int width = getJComponent().getWidth();
        int height = getJComponent().getHeight();
        int x = getJComponent().getX();
        int y = getJComponent().getY();

        int layer = panelManager.getLayer(this);

        return Arrays.asList("rect=" + x + "," + y + ", " + width + "x" + height, "layer=" + layer);
    }

    protected void drawDebugBoundaries(Graphics2D g2)
    {
        Rectangle bounds = getCoords().getBounds();
        g2.setColor(DebugDrawDefines.COLOR_SCALABLECOMPONENT_BOUNDINGBOX);
        g2.drawRect(0, 0, bounds.width - 1, bounds.height - 1);
    }

    private final void drawDebugStrings(Graphics2D g2, List<String> strings)
    {
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Color.white);

        if(strings.size() == 0)
        {
            return;
        }

        g2.setFont(Font.decode("Arial bold 8"));
        int hFat = g2.getFontMetrics().getHeight();
        g2.drawString(strings.get(0), 5, hFat);

        g2.setFont(Font.decode("Arial 8"));
        int hSmall = g2.getFontMetrics().getHeight();

        for (int i = 1; i < strings.size(); i++)
        {
            g2.drawString(strings.get(i), 5, hFat + i * hSmall);
        }
    }

}
