package gent.timdemey.cards.ui;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;

import javax.swing.JLayeredPane;

public class PanelBase extends JLayeredPane implements IHasTransparency
{
    private String debugName = "Unnamed";
    private BufferedImage tile;    
    private float alpha = 1.0f;    
    private float alphaBg = 1.0f;  
    
    public PanelBase (LayoutManager lm)
    {
        setLayout(lm);
    }
    
    public PanelBase (String debugName)
    {
        this.debugName = debugName;
    }
    
    public PanelBase (LayoutManager lm, String debugName)
    {
        setLayout(lm);
        setDebugName(debugName);
    }
    
    public final void setDebugName(String debugName)
    {
        this.debugName = debugName;
    }
    
    public final String getDebugName()
    {
        return debugName;
    }
    
    public void setTile (BufferedImage tile)
    {
        this.tile = tile;
        repaint();
    }
    
    @Override
    public void setAlpha(float alpha)
    {
        this.alpha = alpha;
        repaint();
    }
    
    @Override
    public float getAlpha()
    {
        return alpha;
    }
    @Override
    public void setAlphaBackground(float alpha)
    {
        this.alphaBg = alpha;
        repaint();
    }

@Override 
public float getAlphaBackground()
{
    return alphaBg;
}
    @Override
    public void paint(Graphics g)
    {
        if (alpha < 1.0f)
        {
            Graphics2D g2 = (Graphics2D) g.create();
            
            Composite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            g2.setComposite(composite);
            super.paint(g2);
            
            g2.dispose();
        }
        else
        {
            super.paint(g);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {                
        UICommon.paintBackground(this, g, tile);
        super.paintComponent(g);
    }
    
    @Override
    protected void paintChildren(Graphics g)
    {
        // TODO Auto-generated method stub
        super.paintChildren(g);

        UICommon.paintDebug(this, g, debugName);
    }
}
