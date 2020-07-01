package gent.timdemey.cards.services.scaling.img;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

@SuppressWarnings("serial")
class JScalableImage extends JPanel
{
    private final ScalableImageComponent abstractor;

    JScalableImage(ScalableImageComponent abstractor)
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
