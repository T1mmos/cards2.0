package gent.timdemey.cards.ui.components;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.utils.DebugDrawDefines;

public final class UICommon
{   
    public static void paintDebug(JComponent comp, Graphics g, String debugName)
    {
        if (Services.get(IFrameService.class).getDrawDebug())
        {
            Graphics2D g2 = (Graphics2D) g.create();       
            
            Composite composite = AlphaComposite.getInstance(AlphaComposite.SRC);
            g2.setComposite(composite);
            
            Rectangle bounds = g2.getClipBounds();
            int x = bounds.x;
            int y = bounds.y;
            int w = bounds.width;
            int h = bounds.height;
            
            // draw the full sized outer box
            g2.setStroke(new BasicStroke(4.0f));
            g2.setColor(DebugDrawDefines.COLOR_PANEL_BASE_OUTER);
            g2.drawRect(x, y, w, h);

            // draw the debug name
            if (debugName != null)
            {
                g2.setColor(DebugDrawDefines.COLOR_BG_PANELNAME);
                g2.fillRect(x, y, w, 20);
            
                g2.setColor(DebugDrawDefines.COLOR_TEXT_PANELNAME);
                g2.drawString(debugName.toString(), x+4, y+16);    
            }            
            
            g2.dispose();
        }
    }
}
