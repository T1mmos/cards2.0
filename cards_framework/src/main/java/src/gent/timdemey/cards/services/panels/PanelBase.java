package gent.timdemey.cards.services.panels;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptor;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.utils.DebugDrawDefines;

public class PanelBase extends JComponent
{
    private final PanelDescriptor panelDesc;
    
    public PanelBase (PanelDescriptor panelDesc)
    {
        this.panelDesc = panelDesc;
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        if (Services.get(IFrameService.class).getDrawDebug())
        {
            Graphics2D g2 = (Graphics2D) g.create();
            
            // draw the full sized outer box
            g2.setStroke(new BasicStroke(4.0f));
            g2.setColor(DebugDrawDefines.COLOR_PANEL_BASE_OUTER);
            g2.drawRect(getX(), getY(), getWidth(), getHeight());

            g2.setColor(DebugDrawDefines.COLOR_TEXT_PANELNAME);
            g2.drawString(panelDesc.toString(), 10, 20);
        }
    }
}
