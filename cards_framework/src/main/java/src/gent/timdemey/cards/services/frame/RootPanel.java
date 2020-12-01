package gent.timdemey.cards.services.frame;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;


public class RootPanel extends JPanel
{ 
    private final BufferedImage tile;
    
    RootPanel (BufferedImage tile)
    {
        this.tile = tile;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.clearRect(0, 0, getWidth(), getHeight());
        if (tile != null)
        {
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
    }
}
