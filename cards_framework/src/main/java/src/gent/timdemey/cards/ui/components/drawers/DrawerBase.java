package gent.timdemey.cards.ui.components.drawers;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JComponent;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.ui.components.ext.IHasComponent;
import gent.timdemey.cards.ui.panels.IPanelManager;
import gent.timdemey.cards.utils.ColorUtils;
import gent.timdemey.cards.utils.DebugDrawDefines;

public class DrawerBase<T extends JComponent> implements IDrawer<T>
{
    private boolean mirror = false;
    private float alphaFg = 1.0f;
    private float alphaBg = 1.0f;

    protected T comp;
    private BufferedImage imageBg;
    
    @Override
    public void onAttached(T comp)
    {
        this.comp = comp;
    }
    
    @Override
    public T getJComponent()
    {
        return comp;
    }
    
    @Override
    public final void setMirror(boolean mirror)
    {
        this.mirror = mirror;
    }

    @Override
    public final boolean isMirror()
    {
        return mirror;
    }

    @Override
    public final void setForegroundAlpha(float alpha)
    {
        this.alphaFg = alpha;
    }

    @Override
    public final float getForegroundAlpha()
    {
        return alphaFg;
    }

    @Override
    public final void setBackgroundAlpha(float alpha)
    {
        this.alphaBg = alpha;
    }

    @Override
    public final float getBackgroundAlpha()
    {
        return alphaBg;
    }

    @Override
    public final void setBackgroundImage(BufferedImage image)
    {
        this.imageBg = image;
    }

    @Override
    public final BufferedImage getBackgroundImage()
    {
        return imageBg;
    }

    final void applyAlpha(Graphics2D g, float alpha)
    {
        Composite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g.setComposite(composite);
    }

    public void draw(Graphics g, Consumer<Graphics> superPaintComponent)
    {
        Graphics2D g2 = (Graphics2D) g.create();
        
        drawBackground(g2);
        drawForeground(g2, superPaintComponent);
        drawDebug(g2);
        
        g2.dispose();
    }
    
    public void drawBackground(Graphics2D g)
    {
        Graphics2D g2 = createMirrorGraphics(g);

        // support for background alphaFg
        applyAlpha(g, alphaBg);

        Rectangle bounds = g2.getClipBounds();
        int x = bounds.x;
        int y = bounds.y;
        int w = bounds.width;
        int h = bounds.height;

        // tile the background image if it is set
        if (imageBg != null)
        {
            g2.clearRect(x, y, w, h);
            int tileWidth = imageBg.getWidth();
            int tileHeight = imageBg.getHeight();
            for (int j = y; j < y + h; j += tileHeight)
            {
                for (int i = x; i < x + w; i += tileWidth)
                {
                    g2.drawImage(imageBg, i, j, null);
                }
            }
        }
        else
        {
            Color bgColor = ColorUtils.transparent(comp.getBackground(), alphaBg);
            g2.setColor(bgColor);
            g2.fillRect(x, y, w - 1, h - 1);
        }

        g2.dispose();
    }

    public void drawForeground(Graphics2D g2, Consumer<Graphics> superPaintComponent)
    {
        Graphics2D g = createMirrorGraphics(g2);
        
        applyAlpha(g, alphaFg);

        // by default, draw with the JComponent's given draw function (paintComponent)
        superPaintComponent.accept(g);
        
        g.dispose();
    }

    public void drawDebug(Graphics2D g2)
    {
        if (!Services.get(IFrameService.class).getDrawDebug())
        {
            return;
        }
        
        drawDebugCoords(g2);
        drawDebugBoundaries(g2);

        List<String> debugStrings = getDebugStrings();
        drawDebugStrings(g2, debugStrings);
    }

    public List<String> getDebugStrings()
    {
        int width = comp.getWidth();
        int height = comp.getHeight();
        int x = comp.getX();
        int y = comp.getY();

        String layerStr = "/";
        if (comp instanceof IHasComponent)
        {
            IHasComponent hasComp = (IHasComponent) comp;
            IPanelManager panelMan = hasComp.getComponent().getPanelManager();
            int layer = panelMan.getLayer(comp);
            layerStr = "" + layer;
        }

        return Arrays.asList("rect=" + x + "," + y + ", " + width + "x" + height, "layer=" + layerStr);
    }

    protected void drawDebugCoords(Graphics2D g2)
    {
        Graphics2D g = (Graphics2D) g2.create();
        
        Rectangle rect = comp.getBounds();

        g.setColor(DebugDrawDefines.COLOR_DIMMED_COMPONENT_BACKGROUND);
        g.fillRect(0, 0, rect.width, rect.height);
        
        g.dispose();
    }
    
    protected void drawDebugBoundaries(Graphics2D g2)
    {
        Graphics2D g = (Graphics2D) g2.create();
        
        Rectangle bounds = comp.getBounds();
        g.setColor(DebugDrawDefines.COLOR_SCALABLECOMPONENT_BOUNDINGBOX);
        g.drawRect(0, 0, bounds.width - 1, bounds.height - 1);
        
        g.dispose();
    }

    private final void drawDebugStrings(Graphics2D g2, List<String> strings)
    {
        Graphics2D g = (Graphics2D) g2.create();
        
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(Color.white);

        if (strings.size() == 0)
        {
            return;
        }

        g.setFont(Font.decode("Arial bold 8"));
        int hFat = g.getFontMetrics().getHeight();
        g.drawString(strings.get(0), 5, hFat);

        g2.setFont(Font.decode("Arial 8"));
        int hSmall = g.getFontMetrics().getHeight();

        for (int i = 1; i < strings.size(); i++)
        {
            g.drawString(strings.get(i), 5, hFat + i * hSmall);
        }
        
        g.dispose();
    }

    protected final Graphics2D createMirrorGraphics(Graphics2D g2)
    {
        Graphics2D gMirror = (Graphics2D) g2.create();
        if (isMirror())
        {
            gMirror.scale(-1.0, -1.0);
            gMirror.translate(-comp.getWidth(), -comp.getHeight());
        }
        return gMirror;
    }
}
