package gent.timdemey.cards.ui.components.drawers;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JComponent;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.ui.components.ext.IHasComponent;
import gent.timdemey.cards.ui.components.swing.JSLayeredPane;
import gent.timdemey.cards.utils.ComponentUtils;
import gent.timdemey.cards.utils.DebugDrawDefines;

public class DrawerBase<T extends JComponent> implements IDrawer
{
    private boolean mirror = false;
    private float alphaFg = 1.0f;
    private float alphaBg = 1.0f;

    protected T jcomp;
    private BufferedImage imageBg;
    
    @Override
    public void onAttached(JComponent comp)
    {
        this.jcomp = (T) comp;
    }
    
    @Override
    public T getJComponent()
    {
        return jcomp;
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
        
      /*  if (getJComponent() instanceof Container)
        {
            Container cont = (Container) getJComponent();
            for (Component c : cont.getComponents())
            {
                if (c instanceof IHasDrawer)
                {
                    IHasDrawer hasDrawer = (IHasDrawer) c;
                    hasDrawer.getDrawer().setForegroundAlpha(alpha);
                }
            }
        }*/
        getJComponent().repaint();
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
        getJComponent().repaint();
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
        getJComponent().repaint();
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
        
        if (Services.get(IFrameService.class).getDrawDebug())
        {
            drawDebugCompBox(g2);
        }
        
        drawForeground(g2, superPaintComponent);
        
        g2.dispose();
    }
    
    public void drawChildren(Graphics g, Consumer<Graphics> superPaintChildren)
    {
        Graphics2D g2 = (Graphics2D) g.create();
     
        applyAlpha(g2, alphaFg);
        
        // by default, draw with the JComponent's given draw function (paintChildren)
        superPaintChildren.accept(g2);        
        
        // debug drawing happens even on top of the children
        if (Services.get(IFrameService.class).getDrawDebug())
        {
            List<String> debugStrings = getDebugStrings();
            drawDebugInfoBox(g2, debugStrings);
        }
        
        g2.dispose();
    }
    
    public void drawBackground(Graphics2D g)
    {
        Graphics2D g2 = createMirrorGraphics(g);

        // support for background alpha
        applyAlpha(g2, alphaBg);

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
            // only draw background color if it is set explicitly, otherwise be transparent
            // by skipping the background drawing - omitting the check would 
            // take the parent's color
            if (jcomp.isBackgroundSet())
            {
                g2.setColor(jcomp.getBackground());
                g2.fillRect(x, y, w, h);
            }
        }

        g2.dispose();
    }

    public void drawForeground(Graphics2D g, Consumer<Graphics> superPaintComponent)
    {
        Graphics2D g2 = createMirrorGraphics(g);
        
        applyAlpha(g2, alphaFg);

        // by default, draw with the JComponent's given draw function (paintComponent)
        superPaintComponent.accept(g2);
        
        g2.setColor(Color.red);
        g2.fillRect(25, 25, 100, 100);
        
        g2.dispose();
    }

    protected void addDebugString(List<String> list, Object key, Object value)
    {
        String all =  key + "=" + value;
        list.add(all);
    }
    
    public List<String> getDebugStrings()
    {
        List<String> strs = new ArrayList<>();
        
        // class type
        {
            addDebugString(strs, "Class", jcomp.getClass().getSimpleName());
        }
        
        // ComponentType
        if (jcomp instanceof IHasComponent<?>)
        {
            ComponentType compType = ComponentUtils.getComponentType(jcomp);
            String compTypeStr = compType.typeName;
            if (compType.subType != null)
            {
                compTypeStr = compTypeStr + "/" + compType.subType.typeName;
            }
            addDebugString(strs, "CompType", compTypeStr);
        }
        
        // layer
        if (jcomp.getParent() instanceof JSLayeredPane)
        {
            int layer = ((JSLayeredPane) jcomp.getParent()).getLayer((Component) jcomp);
            addDebugString(strs, "layer", layer);
        }
        
        // coordinates + dimensions
        {
            addDebugString(strs, "width", jcomp.getWidth());
            addDebugString(strs, "height", jcomp.getHeight());
            addDebugString(strs, "x", jcomp.getX());
            addDebugString(strs, "y", jcomp.getY());
        }
        
        return strs;
    }

    protected Color getDebugColor(DebugItems key)
    {
        switch (key)
        {
            case CompboxBackground: 
                return DebugDrawDefines.COLOR_COMPBOX_BACKGROUND_DEFAULT;
            case CompboxOutline: 
                return DebugDrawDefines.COLOR_COMPBOX_OUTLINE_DEFAULT;
            case PaddingBoxBackground:
                return DebugDrawDefines.COLOR_PADBOX_BACKGROUND_DEFAULT;
            case PaddingBoxOutline:
                return DebugDrawDefines.COLOR_PADBOX_OUTLINE_DEFAULT;
            case InfoBoxBackground:
            {
                int parents = 0;
                Component comp = getJComponent().getParent();
                while (comp != null)
                {
                    parents++;
                    comp = comp.getParent();
                }
                
                parents -= 3; // JLayeredPane, JRootPane, JFrame
                
                switch (parents)
                {
                case 0:
                    return DebugDrawDefines.COLOR_INFOBOX_BACKGROUND_LVL1;
                case 1:
                    return DebugDrawDefines.COLOR_INFOBOX_BACKGROUND_LVL2;
                case 2:
                default:
                    return DebugDrawDefines.COLOR_INFOBOX_BACKGROUND_LVL3;
                }
                
            }   
            case InfoBoxOutline:
                return DebugDrawDefines.COLOR_INFOBOX_OUTLINE_DEFAULT;
            case InfoBoxText:
                return DebugDrawDefines.COLOR_INFOBOX_TEXT_DEFAULT;
            default:
                return DebugDrawDefines.COLOR_DEFAULT;
        }
    }
    
    protected Stroke getDebugStroke(DebugItems key)
    {
        switch (key)
        {
            case CompboxOutline:
                return DebugDrawDefines.STROKE_DEFAULT;
            case PaddingBoxOutline:
                return DebugDrawDefines.STROKE_DASHED;
            default:
                return DebugDrawDefines.STROKE_DEFAULT;        
        }        
    }
    
    protected void drawDebugCompBox(Graphics2D g2)
    {
        Graphics2D g = (Graphics2D) g2.create();

        // background
        {
            Rectangle rect = jcomp.getBounds();
            Color color = getDebugColor(DebugItems.CompboxBackground);
            g.setColor(color);
            g.fillRect(0, 0, rect.width, rect.height);    
        }
        
        // outline
        {
            Rectangle bounds = jcomp.getBounds();
            Color color = getDebugColor(DebugItems.CompboxOutline);
            g.setColor(color);
            g.drawRect(0, 0, bounds.width - 1, bounds.height - 1);
        }        
        
        g.dispose();
    }

    private final void drawDebugInfoBox(Graphics2D g2, List<String> strings)
    {
        Graphics2D g = (Graphics2D) g2.create();
        
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int cnt = strings.size();
        if (cnt == 0)
        {
            return;
        }

        Font fontFat = Font.decode("Arial bold 7");
        Font fontSmall = Font.decode("Arial 7");
        
        // first derive some metrics before actual drawing
        int hFat, hSmall, wMax, hMax, padding = 5;
        {
            g.setFont(fontFat);
            hFat = g.getFontMetrics().getHeight();
            int wFat = g.getFontMetrics().stringWidth(strings.get(0));

            g.setFont(fontSmall);
            hSmall = g.getFontMetrics().getHeight();
            hMax = hFat + (cnt - 1) * hSmall;
            wMax = wFat;
            for (int i = 1; i < cnt; i++)
            {
                wMax = Math.max(wMax, g.getFontMetrics().stringWidth(strings.get(i)));
            }
        }
        
        // draw nearly non-transparent, bouding box
        g.setColor(getDebugColor(DebugItems.InfoBoxBackground));
        g.fillRect(padding, padding, wMax+ 2*padding, hMax+ 2*padding);
        g.setColor(getDebugColor(DebugItems.InfoBoxText));
        g.drawRect(padding, padding, wMax + 2*padding, hMax + 2*padding);
                
        // draw actual strings
        g.setFont(fontFat);
        g.drawString(strings.get(0), 2*padding, padding + hFat);
        g.setFont(fontSmall);
        for (int i = 1; i < cnt; i++)
        {
            g.drawString(strings.get(i), 2*padding, padding + hFat + i * hSmall);
        }
        
        g.dispose();
    }

    protected final Graphics2D createMirrorGraphics(Graphics2D g2)
    {
        Graphics2D gMirror = (Graphics2D) g2.create();
        if (isMirror())
        {
            gMirror.scale(-1.0, -1.0);
            gMirror.translate(-jcomp.getWidth(), -jcomp.getHeight());
        }
        return gMirror;
    }
}
