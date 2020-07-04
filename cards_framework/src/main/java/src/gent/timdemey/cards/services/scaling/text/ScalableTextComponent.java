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
import java.util.UUID;

import javax.swing.JComponent;

import gent.timdemey.cards.services.scaling.ScalableComponent;
import gent.timdemey.cards.services.scaling.ScalableResource;

public abstract class ScalableTextComponent extends ScalableComponent<Font>
{
    private String text;
    private Color textColor;

    public ScalableTextComponent(UUID id, String text, ScalableFontResource fontRes)
    {
        super(id, fontRes);
        this.text = text;
    }

    @Override
    protected String[] addDebugStrings()
    {
        return null;
    }
    
    public final String getText()
    {
        return text;
    }
    
    protected final void setText(String text)
    {
        this.text = text;
    }
    
    public void setTextColor(Color color)
    {
        textColor = color;
    }
    
    @Override
    protected void draw(Graphics2D g)
    {
        Graphics2D g2d = (Graphics2D)g.create();
        
        Rectangle bounds = getBounds();
        // jlabel bounding box
        g2d.setColor(Color.green);
        g2d.fillRect(0, 0, bounds.width, bounds.height);
        g2d.setColor(Color.blue);
        g2d.drawRect(0, 0, bounds.width - 1, bounds.height - 1);
                
        ScalableResource<Font> fontRes = (ScalableResource<Font>) super.getScalableResources().get(0);
        Dimension fontDim = new Dimension(0, bounds.height - 10);
        Font font = fontRes.get(fontDim);
        g2d.setFont(font);
        // text bounding box
        FontMetrics fm = g2d.getFontMetrics();
        Rectangle2D strRect = fm.getStringBounds(getText(), g);        
        g2d.setColor(Color.red);
        int w = (int)strRect.getWidth() - 1;
        int h = (int)strRect.getHeight() -1;
        int x = (bounds.width - w) / 2;
        int y = (bounds.height - h) / 2;        
        g2d.setColor(Color.black);
        g2d.drawRect(x, y, w, h);
        
        int ascent = fm.getAscent();
        g2d.translate(x, +ascent + y);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        
        // text
        FontRenderContext frc = g2d.getFontRenderContext();
        
        TextLayout tl = new TextLayout(text, g2d.getFont(), frc); 
        Shape shape = tl.getOutline(null);
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(Color.red);
        g2d.fill(shape);
        g2d.setColor(Color.black);
        g2d.draw(shape);
    }
}
