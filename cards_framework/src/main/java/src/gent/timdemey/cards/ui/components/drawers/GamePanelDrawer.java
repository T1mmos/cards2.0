package gent.timdemey.cards.ui.components.drawers;

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
    protected void drawDebugCompBox(Graphics2D g)
    {
        super.drawDebugCompBox(g);
                
        if (!Services.get(IFrameService.class).getDrawDebug())
        {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        
        IPositionService posMan = Services.get(IPositionService.class);
        Coords.Absolute coords = posMan.getPackedCoords();
                    
        // draw the packed box        
        g2.setColor(getDebugColor(DebugItems.PaddingBoxOutline));
        g2.setStroke(DebugDrawDefines.STROKE_DASHED);
        g2.drawRect(coords.x, coords.y, coords.w, coords.h);
                
        g.dispose();
    }
}
