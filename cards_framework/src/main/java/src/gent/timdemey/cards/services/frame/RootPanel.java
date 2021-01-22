package gent.timdemey.cards.services.frame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class RootPanel extends JPanel
{ 
    private final BufferedImage tile;
    private boolean transparency = false;
    
    RootPanel ()
    {
        this.tile = null;
    }
    
    RootPanel (BufferedImage tile)
    {
        if (tile == null)
        {
            throw new NullPointerException("tile");
        }
        this.tile = tile;
    }
    
    @Override
    public void setBackground(Color background)
    {        
        transparency = background.getAlpha() < 255;
        
        if (transparency)
        {
            super.setOpaque(false);
        }
        
        super.setBackground(background);
    }
    
    @Override
    public void setOpaque(boolean isOpaque)
    {
        // ignore
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
            
        
       
        if (tile != null)
        {
            Graphics2D g2 = (Graphics2D) g;
            g2.clearRect(0, 0, getWidth(), getHeight());
            int tileWidth = tile.getWidth();
            int tileHeight = tile.getHeight();
            for (int y = 0; y < getHeight(); y += tileHeight)
            {
                for (int x = 0; x < getWidth(); x += tileWidth)
                {
                    g2.drawImage(tile, x, y, this);
                }
            }
        }
        else if (transparency)
        {
            Graphics2D g2 = (Graphics2D) g.create();
            
            // draw semi-transparency
            g2.setColor(getBackground());
            g2.fillRect(0,0, getWidth() - 1, getHeight() - 1);
        }
    }
}
