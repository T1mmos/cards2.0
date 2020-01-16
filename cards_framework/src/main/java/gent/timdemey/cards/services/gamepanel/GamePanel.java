package gent.timdemey.cards.services.gamepanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JLayeredPane;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.IGamePanelManager;
import gent.timdemey.cards.services.IPositionManager;

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

        Graphics2D g2 = (Graphics2D) g;
        IPositionManager posMan = Services.get(IPositionManager.class);
        Rectangle rect = posMan.getBounds();

        if (Services.get(IGamePanelManager.class).getDrawDebug())
        {
            g2.setStroke(new BasicStroke(2.0f));
            g2.setColor(Color.ORANGE);

            g2.drawRect(rect.x, rect.y, rect.width, rect.height);
        }
    }
}
