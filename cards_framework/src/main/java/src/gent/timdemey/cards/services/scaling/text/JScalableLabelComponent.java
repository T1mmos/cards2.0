package gent.timdemey.cards.services.scaling.text;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
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
    public Dimension getPreferredSize()
    {
        Dimension prefSize = super.getPreferredSize();
        return prefSize;
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g.create();
        FontMetrics fm = g2d.getFontMetrics();
        
        g2d.setColor(Color.green);
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();
      //  g2d.fillRect(x, y, w, h);
        
        int ascent = fm.getAscent();
        int strokeW = 4;
        g2d.translate(- strokeW / 2, +ascent - strokeW / 2);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        
        g2d.setColor(Color.black);
        FontRenderContext frc = g2d.getFontRenderContext();
        TextLayout tl = new TextLayout(super.getText(), g2d.getFont(), frc);
        Shape shape = tl.getOutline(null);
        g2d.setStroke(new BasicStroke(3));
        g2d.draw(shape);
     //   g2d.fill(shape);
        
        super.paintComponent(g);
    }
}
