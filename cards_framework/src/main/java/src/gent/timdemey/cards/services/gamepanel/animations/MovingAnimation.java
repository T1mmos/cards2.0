package gent.timdemey.cards.services.gamepanel.animations;

import java.awt.Point;
import java.awt.Rectangle;

import gent.timdemey.cards.services.scaleman.IScalableComponent;

public class MovingAnimation implements IAnimation
{
    final Point pos_start;
    final Point pos_end;
    
    public MovingAnimation (int startx, int starty, int endx, int endy)
    {
        this (new Point(startx, starty), new Point(endx, endy));
    }
    
    public MovingAnimation (Point start, Point end)
    {
        this.pos_start = start;
        this.pos_end = end;
    }
    
    @Override
    public void tick(double frac, IScalableComponent comp)
    {
        int posx = (int) ( (1.0 - frac) * pos_start.getX() + frac * pos_end.getX() );
        int posy = (int) ( (1.0 - frac) * pos_start.getY() + frac * pos_end.getY() );
        Rectangle bounds = comp.getBounds();
        comp.setBounds(posx, posy, bounds.width,  bounds.height);
    }
}
