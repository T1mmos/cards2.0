package gent.timdemey.cards.services.gamepanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLayeredPane;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.contract.Coords;
import gent.timdemey.cards.services.interfaces.IGamePanelService;
import gent.timdemey.cards.services.interfaces.IPositionService;

public class GamePanel extends JLayeredPane
{

    GamePanel()
    {
        setLayout(null); // absolute positioning
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);


        if (Services.get(IGamePanelService.class).getDrawDebug())
        {
            Graphics2D g2 = (Graphics2D) g;
            IPositionService posMan = Services.get(IPositionService.class);
            Coords.Absolute coords = posMan.getPackedCoords();
            
            // draw the full sized outer box
            g2.setStroke(new BasicStroke(4.0f));
            g2.setColor(Color.RED);
            g2.drawRect(getX(), getY(), getWidth(), getHeight());
            
            // draw the packed box
            g2.setStroke(new BasicStroke(2.0f));
            g2.setColor(Color.ORANGE);
            g2.drawRect(coords.x, coords.y, coords.w, coords.h);
        }
    }
}
