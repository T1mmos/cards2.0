package gent.timdemey.cards.ui.components;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

final class JSComponent extends JComponent  
{
    private final SComponent sComp;

    JSComponent(SComponent abstractor)
    {
        this.sComp = abstractor;
        setOpaque(false);
    }
    
    public SComponent getSComponent()
    {
        return sComp;
    }    
    
    @Override
    protected final void paintComponent(Graphics g)
    {
        // create graphics
        Graphics2D g2 = (Graphics2D) g.create();
        
        sComp.drawAll(g2);
        
        // dispose graphics
        g2.dispose();
    }
}
