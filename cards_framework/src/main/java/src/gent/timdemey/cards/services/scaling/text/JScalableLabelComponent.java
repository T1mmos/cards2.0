package gent.timdemey.cards.services.scaling.text;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;

import javax.swing.JLabel;

public class JScalableLabelComponent extends JLabel
{
    
    public JScalableLabelComponent(String text)
    {
        super(text);
    }
    
    @Override
    public void paint(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g.create();
        FontMetrics fm = g2d.getFontMetrics();
        g2d.translate(0, fm.getAscent());
        
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        g2d.setColor(Color.black);
        FontRenderContext frc = g2d.getFontRenderContext();
        TextLayout tl = new TextLayout(super.getText(), g2d.getFont(), frc);
        Shape shape = tl.getOutline(null);
        g2d.setStroke(new BasicStroke(getHeight() / 4));
        g2d.draw(shape);
        g2d.fill(shape);
        
        
        super.paint(g);
    }
}
