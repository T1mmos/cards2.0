package gent.timdemey.cards.services.animation;

import javax.swing.JComponent;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.contract.Coords;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.ui.components.ext.IHasComponent;

public class MovingAnimation implements IAnimation
{
    public MovingAnimation ()
    {
    }
    
    @Override
    public void tick(JComponent jcomp, double frac, AnimationStart animStart)
    {        
        // convert the relative coordinates of the component at the start of the animation
        // into absolute coordinates in the current reference frame, and interpolate 
        // with the destination coordinates as returned by the IPositionService (which are
        // already set in the current reference frame).
        IPositionService posServ = Services.get(IPositionService.class);
        
        Coords.Absolute coords_src = posServ.getAbsoluteCoords(animStart.relcoords);
        Coords.Absolute coords_dst = posServ.getLayeredArea(jcomp).abscoords_dst;          
        Coords.Absolute coords_interp = Coords.interpolate(frac, coords_src, coords_dst);   
        
        ((IHasComponent<?>) jcomp).getComponent().setAbsCoords(coords_interp);
        
        jcomp.validate();
    }
}