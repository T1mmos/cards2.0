package gent.timdemey.cards.services.contract;

import java.awt.Point;
import java.awt.Rectangle;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.interfaces.IAnimationService;
import gent.timdemey.cards.services.scaling.IScalableComponent;

public class MovingAnimation implements IAnimation
{
    public MovingAnimation ()
    {
    }
    
    @Override
    public void tick(double frac, IScalableComponent comp)
    {
        IAnimationService animServ = Services.get(IAnimationService.class);
        animServ.getAnimationTick(comp)
        int posx = (int) ( (1.0 - frac) * pos_start.getX() + frac * pos_end.getX() );
        int posy = (int) ( (1.0 - frac) * pos_start.getY() + frac * pos_end.getY() );
        Rectangle bounds = comp.getBounds();
        comp.setBounds(posx, posy, bounds.width,  bounds.height);
    }
}