package gent.timdemey.cards.ui.components.drawers;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

import javax.swing.JLayeredPane;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.contract.Coords;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.utils.DebugDrawDefines;

public class GamePanelDrawer extends DrawerBase<JLayeredPane>
{
    
    @Override
    protected void drawDebugBoundaries(Graphics2D g)
    {
        super.drawDebugBoundaries(g);
                
        if (!Services.get(IFrameService.class).getDrawDebug())
        {
            return;
        }

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
        
        g.dispose();
    }
}
