package gent.timdemey.cards.services.scaling;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public final class JScalableComponent extends JPanel
{
    private final ScalableComponent<?> abstractor;

    public JScalableComponent(ScalableComponent<?> abstractor)
    {
        this.abstractor = abstractor;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        abstractor.draw(g2);
        abstractor.drawDebug(g2);
        
        g2.dispose();
    }
}
