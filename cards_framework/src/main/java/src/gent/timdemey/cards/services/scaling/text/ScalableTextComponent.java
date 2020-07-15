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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.services.contract.GetCardScaleInfoRequest;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.scaling.ScalableComponent;

public abstract class ScalableTextComponent extends ScalableComponent
{
    private final ScalableFontResource fontResource;
    private String text;
    private Color textColor;
    private TextAlignment alignment;

    public ScalableTextComponent(UUID id, String text, ScalableFontResource fontResource)
    {
        super(id);
        this.text = text;
        this.fontResource = fontResource;
        setAlignment(TextAlignment.Center);
    }

    @Override
    protected List<String> getDebugStrings()
    {
        List<String> allStrings = new ArrayList<>(super.getDebugStrings());

        allStrings.add(String.format("font=%s", fontResource.getResource().filename));
        allStrings.add(String.format("fontsize=%s", getFont().getSize()));

        return allStrings;
    }

    @Override
    protected void drawDebugBoundaries(Graphics2D g2)
    {
        super.drawDebugBoundaries(g2);

        // jlabel bounding box
        Rectangle bounds = getCoords().getBounds();
        g2.setColor(Color.blue);
        g2.drawRect(0, 0, bounds.width - 1, bounds.height - 1);

        // text bounding box
        Rectangle tb = getTextBounds(g2);
        g2.setColor(Color.black);
        g2.drawRect(tb.x, tb.y, tb.width, tb.height);
    }

    protected int getFontHeight()
    {
        return 10;
    }
    
    private Font getFont()
    {
        int fontHeight = getFontHeight();
        Dimension fontDim = new Dimension(0, fontHeight);
        Font font = fontResource.get(fontDim);
        return font;
    }

    private Rectangle getTextBounds(Graphics2D g2)
    {
        Rectangle bounds = getCoords().getBounds();

        Font font = getFont();
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

    protected final void setText(String text)
    {
        this.text = text;
        getComponent().repaint();
    }

    public void setTextColor(Color color)
    {
        textColor = color;
    }

    public final void setAlignment(TextAlignment alignment)
    {
        this.alignment = alignment;
    }

    @Override
    protected void draw(Graphics2D g)
    {
        Graphics2D g2d = (Graphics2D) g.create();

        Font font = getFont();
        
        g2d.setFont(font);

        Rectangle tb = getTextBounds(g);

        FontMetrics fm = g.getFontMetrics();
        int ascent = fm.getAscent();
        int descent = fm.getDescent();
        g2d.translate(tb.x, +ascent -descent + tb.y);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // text
        FontRenderContext frc = g2d.getFontRenderContext();

        TextLayout tl = new TextLayout(text, g2d.getFont(), frc);
        Shape shape = tl.getOutline(null);

        float strokeWidth = 1.0f * font.getSize() / 16;
        g2d.setStroke(new BasicStroke(strokeWidth));
        g2d.setColor(textColor);
        g2d.fill(shape);
        g2d.setColor(Color.darkGray);
        g2d.draw(shape);
    }
}
