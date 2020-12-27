package gent.timdemey.cards.services.panels;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;

import javax.swing.JLayeredPane;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptor;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.utils.DebugDrawDefines;

public class PanelBase extends JLayeredPane
{
    public final PanelDescriptor panelDesc;
    
    public PanelBase (PanelDescriptor panelDesc)
    {
        this.panelDesc = panelDesc;
    }
    
    public PanelBase (PanelDescriptor panelDesc, LayoutManager lm)
    {
        this.panelDesc = panelDesc;
        setLayout(lm);
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
