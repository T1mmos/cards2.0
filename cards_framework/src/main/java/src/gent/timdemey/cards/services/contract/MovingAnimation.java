package gent.timdemey.cards.services.contract;

import gent.timdemey.cards.Services;
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
        // convert the relative coordinates of the component at the start of the animation
        // into absolute coordinates in the current reference frame, and interpolate 
        // with the destination coordinates as returned by the IPositionService (which are
        // already set in the current reference frame).
        IPositionService posServ = Services.get(IPositionService.class);
        
        Coords.Absolute coords_src = posServ.getAbsoluteCoords(animStart.relcoords);
        Coords.Absolute coords_dst = posServ.getEndLayeredArea(comp).coords;          
        Coords.Absolute coords_interp = Coords.interpolate(frac, coords_src, coords_dst);   
        
        comp.setCoords(coords_interp);
        comp.getComponent().validate();
    }
}