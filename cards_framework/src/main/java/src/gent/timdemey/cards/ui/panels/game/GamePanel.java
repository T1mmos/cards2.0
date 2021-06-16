package gent.timdemey.cards.ui.panels.game;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.contract.Coords;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.ui.components.JSLayeredPane;
import gent.timdemey.cards.utils.DebugDrawDefines;

public class GamePanel extends JSLayeredPane
{
    GamePanel()
    {
        super(PanelDescriptors.Game.id);

        setLayout(null); // absolute positioning
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        // draws outer box in draw debug mode
        super.paintComponent(g);
        
        if (Services.get(IFrameService.class).getDrawDebug())
        {
            Graphics2D g2 = (Graphics2D) g.create();
            IPositionService posMan = Services.get(IPositionService.class);
            Coords.Absolute coords = posMan.getPackedCoords();
                        
            // draw the packed box
            g2.setStroke(new BasicStroke(2.0f));
            g2.setColor(DebugDrawDefines.COLOR_PANEL_GAME_INNER);
            g2.setStroke(DebugDrawDefines.STROKE_DASHED);
            g2.drawRect(coords.x, coords.y, coords.w, coords.h);
            
            g2.setColor(DebugDrawDefines.COLOR_TEXT_PANELNAME);
            g2.drawString("GamePanel", 10, 20);
        }
    }
}
