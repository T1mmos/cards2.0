package gent.timdemey.cards.ui.components.drawers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JLabel;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.contract.GetResourceResponse;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.ui.components.SFontResource;
import gent.timdemey.cards.ui.components.swing.JSLabel;
import gent.timdemey.cards.utils.ComponentUtils;
import gent.timdemey.cards.utils.DebugDrawDefines;

public final class ScaledTextDrawer extends DrawerBase<JSLabel>
{    
    private Color textColorOutline;
    private Color textColorOutline_mouseover;
    private Color textColorOutline_mousePressed;
    
    private Color textColorFg_mouseover;
    private Color textColorFg_mousePressed;
        
    private final SFontResource fontResource;
    
    // caches the drawn component so upon resizements, we are still able to 
    // draw a fast-scaled image until the rescaled resource is set
    private BufferedImage bufferedImage = null;
    
    private boolean mouseOver = false; 
    private boolean mousePressed = false;
        
    public ScaledTextDrawer(SFontResource fontResource)
    {
        this.fontResource = fontResource;
    }
    
    @Override
    public void onAttached(JComponent comp)
    {
        super.onAttached(comp);
        
        getJComponent().setHorizontalTextPosition(JLabel.CENTER);
    }

    @Override
    public List<String> getDebugStrings()
    {   
        List<String> allStrings = new ArrayList<>(super.getDebugStrings());

        allStrings.add(String.format("font=%s", fontResource.getResource().filename));
        allStrings.add(String.format("fontsize=%s", getFont(jcomp).resource.getSize()));

        return allStrings;
    }

    @Override
    public void drawDebugCompBox(Graphics2D g2)
    {        
        Rectangle bounds = jcomp.getBounds();
        
        // jlabel bounding box
        {
            Graphics2D g = (Graphics2D) g2.create();
            g.setStroke(new BasicStroke());
            g.setColor(getDebugColor(DebugItems.CompboxOutline));
            g.drawRect(0, 0, bounds.width - 1, bounds.height - 1);            
        }        

        // text soft bounding box
        {
            Graphics2D g = (Graphics2D) g2.create();
            Rectangle tb = getTextBounds(g, jcomp);
            g.setColor(getDebugColor(DebugItems.PaddingBoxOutline));
            g.setStroke(getDebugStroke(DebugItems.PaddingBoxOutline));
            g.drawRect(tb.x, tb.y, tb.width - 1, tb.height - 1);
        }        
    }
   
    
    private GetResourceResponse<Font> getFont(JLabel comp)
    {
        ComponentType compType = ComponentUtils.getComponentType(comp);        

        IPositionService posServ = Services.get(IPositionService.class);
        Dimension resDim = posServ.getResourceDimension(compType);
        
        // only interested in height for fonts as the width is depending on the text
        GetResourceResponse<Font> resp = fontResource.get(resDim);
        return resp;
    }

    private Rectangle getTextBounds(Graphics2D g2, JLabel comp)
    {
        Rectangle bounds = comp.getBounds();

        Font font = getFont(comp).resource;
        g2.setFont(font);
        // text bounding box
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D strRect = fm.getStringBounds(comp.getText(), g2);

        int w = (int) strRect.getWidth();
        int h = (int) strRect.getHeight();
        
        if (h > bounds.height)
        {
            h = bounds.height;
        }

        int x;
        if(comp.getHorizontalTextPosition() == JSLabel.LEFT)
        {
            x = 0;
        }
        else if(comp.getHorizontalTextPosition() == JSLabel.CENTER)
        {
            x = (bounds.width - w) / 2;
        }
        else // right
        {
            x = bounds.width - w - 1;
        }
        int y = (bounds.height - h) / 2;
        
        return new Rectangle(x, y, w, h);
    }

    public void setOuterColor(Color color)
    {
        textColorOutline = color;
    }
    
    public void setInnerColorMouseOver(Color color)
    {
        textColorFg_mouseover = color;
    }
    
    public void setOuterColorMouseOver(Color color)
    {
        textColorOutline_mouseover = color;
    }

    @Override
    public void drawForeground(Graphics2D g2, Consumer<Graphics> superPaintComponent)
    {        
        Graphics2D g = (Graphics2D) g2.create();
        
        applyAlpha(g, getForegroundAlpha());

        GetResourceResponse<Font> resp = getFont(jcomp);
        Dimension dim = ComponentUtils.getComponent(jcomp).getAbsCoords().getSize();
        if ((bufferedImage == null || (bufferedImage.getWidth() != dim.width || bufferedImage.getHeight() != dim.height)) && resp.found)
        {
            // update the buffered image
            Font font = resp.resource;
            
            bufferedImage = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);            
            Graphics2D g2d = bufferedImage.createGraphics();
            
            g2d.setFont(font);

            Rectangle tb = getTextBounds(g, jcomp);
            FontMetrics fm = g.getFontMetrics();
            int descent = fm.getDescent();
            g2d.translate(tb.x, + tb.y + tb.height - descent);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // text
            String text = jcomp.getText();
            if (text != null && !text.isEmpty())
            {
                FontRenderContext frc = g2d.getFontRenderContext();

                TextLayout tl = new TextLayout(text, g2d.getFont(), frc);
                Shape shape = tl.getOutline(null);

                float strokeWidth = 1.0f * font.getSize() / 16;
                g2d.setStroke(new BasicStroke(strokeWidth));
                g2d.setColor(getInnerColor(jcomp));
                g2d.fill(shape);
                g2d.setColor(getOutlineColor());
                g2d.draw(shape);                    
            }               
            
            g2d.dispose();
        }
        
        // draw the buffered image onto the graphics context and scale if necessary        
        g.drawImage(bufferedImage, 0, 0, dim.width, dim.height, null);
    }
    
    private Color getInnerColor(JLabel label)
    {
        if (mousePressed && textColorFg_mousePressed != null)
        {
            return textColorFg_mousePressed;         
        }        
        if (mouseOver && textColorFg_mouseover != null)
        {    
            return textColorFg_mouseover;            
        }
        
        // default
        return label.getForeground();
    }
    
    private Color getOutlineColor()
    {
        if (mousePressed && textColorOutline_mousePressed != null)
        {
            return textColorOutline_mousePressed;         
        }  
        if (mouseOver && textColorOutline_mouseover != null)
        {
            return textColorOutline_mouseover;
        }
        
        // default
        return textColorOutline;
    }

    void setMouseInside(boolean inside)
    {
        if (mouseOver != inside)
        {
            mouseOver = inside;
        }
    }
    
    void setMousePressed(boolean pressed)
    {
        if (mousePressed != pressed)
        {
            mousePressed = pressed;
        }        
    }
    
    @Override
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
                return DebugDrawDefines.COLOR_INFOBOX_BACKGROUND_DEFAULT;
            case InfoBoxOutline:
                return DebugDrawDefines.COLOR_INFOBOX_OUTLINE_DEFAULT;
            case InfoBoxText:
                return DebugDrawDefines.COLOR_INFOBOX_TEXT_DEFAULT;
        }
        return super.getDebugColor(key);
    }
}
