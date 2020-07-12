package gent.timdemey.cards.services.contract;

import java.awt.Rectangle;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.interfaces.IAnimationService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.scaling.IScalableComponent;

public class MovingAnimation implements IAnimation
{
    public MovingAnimation ()
    {
    }
    
    @Override
    public void tick(IScalableComponent comp, double frac, AnimationStart animStart)
    {
        IAnimationService animServ = Services.get(IAnimationService.class);
        AnimationDescriptor x = animServ.getAnimationDescriptor(comp);
        
        // get the absolute coordinates of the component at the start of the animation,
        // which are dynamic values according to the size of the container, which 
        // can change while stuff is being animated. Hail relative coordinates!
        IPositionService posServ = Services.get(IPositionService.class);
        Coords absCoords = posServ.getAbsolute(animStart.coords);
       
        
        
        animServ.getAnimationTick(comp)
        int posx = (int) ( (1.0 - frac) * pos_start.getX() + frac * pos_end.getX() );
        int posy = (int) ( (1.0 - frac) * pos_start.getY() + frac * pos_end.getY() );
        Rectangle bounds = comp.getBounds();
        comp.setBounds(posx, posy, bounds.width,  bounds.height);
    }
}