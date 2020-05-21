package gent.timdemey.cards.services.gamepanel.animations;

import java.awt.Point;

import javax.swing.JComponent;

public class MovingAnimation implements IAnimation
{
    final Point pos_start;
    final Point pos_end;
    
    public MovingAnimation (Point start, Point end)
    {
        this.pos_start = start;
        this.pos_end = end;
    }
    
    @Override
    public void tick(double frac, JComponent comp)
    {
        int posx = (int) ( (1.0 - frac) * pos_start.getX() + frac * pos_end.getX() );
        int posy = (int) ( (1.0 - frac) * pos_start.getY() + frac * pos_end.getY() );
        
        comp.setBounds(posx, posy, comp.getWidth(), comp.getHeight());
    }
}
