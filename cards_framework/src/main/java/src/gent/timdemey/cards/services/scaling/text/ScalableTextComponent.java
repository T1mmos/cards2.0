package gent.timdemey.cards.services.scaling.text;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
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
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.contract.GetResourceResponse;
import gent.timdemey.cards.services.contract.descriptors.ComponentDescriptor;
import gent.timdemey.cards.services.contract.descriptors.ResourceUsage;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.scaling.ScalableComponent;
import gent.timdemey.cards.services.scaling.debug.DebugDrawDefines;

public final class ScalableTextComponent extends ScalableComponent
{    
    private String text;
    private Color textColorInner;
    private Color textColorOuter;
    private TextAlignment alignment;
    private final ComponentDescriptor compDescriptor;
    private final ScalableFontResource fontResource;
    
    // caches the drawn component so upon resizements, we are still able to 
    // draw a fast-scaled image until the rescaled resource is set
    private BufferedImage bufferedImage = null;
    

    public ScalableTextComponent(UUID id, String text, ComponentDescriptor descriptor, ScalableFontResource fontResource)
    {
        super(id, descriptor);
        this.text = text;
        this.compDescriptor = descriptor;
        this.fontResource = fontResource;
        setAlignment(TextAlignment.Center);
    }

    @Override
    protected List<String> getDebugStrings()
    {
        List<String> allStrings = new ArrayList<>(super.getDebugStrings());

        allStrings.add(String.format("font=%s", fontResource.getResource().filename));
        allStrings.add(String.format("fontsize=%s", getFont().resource.getSize()));

        return allStrings;
    }

    @Override
    protected void drawDebugBoundaries(Graphics2D g2)
    {
        // jlabel bounding box
        {
            Graphics2D g = (Graphics2D) g2.create();
            Rectangle bounds = getCoords().getBounds();
            g.setStroke(new BasicStroke());
            g.setColor(DebugDrawDefines.COLOR_SCALABLETEXTCOMPONENT_BOUNDINGBOX);
            g.drawRect(0, 0, bounds.width - 1, bounds.height - 1);
        }
        

        // text soft bounding box
        {
            Graphics2D g = (Graphics2D) g2.create();
            Rectangle tb = getTextBounds(g);
            g.setColor(DebugDrawDefines.COLOR_SCALABLETEXTCOMPONENT_TEXTBOX);
            g.setStroke(DebugDrawDefines.STROKE_DASHED);
            g.drawRect(tb.x, tb.y, tb.width, tb.height);
        }
        
    }
        
    private GetResourceResponse<Font> getFont()
    {
        IPositionService posServ = Services.get(IPositionService.class);
        Dimension resDim = posServ.getResourceDimension(compDescriptor, ResourceUsage.MAIN_TEXT);
        
        // only interested in height for fonts as the width is depending on the text
        GetResourceResponse<Font> resp = fontResource.get(resDim);
        return resp;
    }

    private Rectangle getTextBounds(Graphics2D g2)
    {
        Rectangle bounds = getCoords().getBounds();

        Font font = getFont().resource;
        g2.setFont(font);
        // text bounding box
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D strRect = fm.getStringBounds(getText(), g2);

        int w = (int) strRect.getWidth() - 1;
        int h = (int) strRect.getHeight() - 1;

        int x;
        if(alignment == TextAlignment.Left)
        {
            x = 0;
        }
        else if(alignment == TextAlignment.Center)
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

    public final String getText()
    {
        return text;
    }

    public final void setText(String text)
    {
        this.text = text;
        getComponent().repaint();
    }

    public void setInnerColor(Color color)
    {
        textColorInner = color;
    }
    
    public void setOuterColor(Color color)
    {
        textColorOuter = color;
    }

    public final void setAlignment(TextAlignment alignment)
    {
        this.alignment = alignment;
    }

    @Override
    protected void draw(Graphics2D g)
    {
        GetResourceResponse<Font> resp = getFont();
        Dimension dim = getCoords().getSize();
        if (resp.found)
        {
            // update the buffered image
            Font font = resp.resource;
            
            bufferedImage = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);            
            Graphics2D g2d = bufferedImage.createGraphics();
            
            g2d.setFont(font);

            Rectangle tb = getTextBounds(g);
            FontMetrics fm = g.getFontMetrics();
            int descent = fm.getDescent();
            g2d.translate(tb.x, + tb.y + tb.height - descent);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // text
            FontRenderContext frc = g2d.getFontRenderContext();

            TextLayout tl = new TextLayout(text, g2d.getFont(), frc);
            Shape shape = tl.getOutline(null);

            float strokeWidth = 1.0f * font.getSize() / 16;
            g2d.setStroke(new BasicStroke(strokeWidth));
            g2d.setColor(textColorInner);
            g2d.fill(shape);
            g2d.setColor(textColorOuter);
            g2d.draw(shape);
            
            g2d.dispose();
        }
        
        // draw the buffered image onto the graphics context and scale if necessary        
        g.drawImage(bufferedImage, 0, 0, dim.width, dim.height, null);
    }
}
