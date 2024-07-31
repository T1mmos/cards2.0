package gent.timdemey.cards.services.animation;

import javax.swing.JComponent;


import gent.timdemey.cards.services.contract.Coords;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.ui.components.ext.IHasComponent;

public class MovingAnimation implements IAnimation
{
    private final IPositionService _PositionService;
    
    public MovingAnimation (IPositionService positionService)
    {
        this._PositionService = positionService;
    }
    
    @Override
    public void tick(JComponent jcomp, double frac, AnimationStart animStart)
    {        
        // convert the relative coordinates of the component at the start of the animation
        // into absolute coordinates in the current reference frame, and interpolate 
        // with the destination coordinates as returned by the IPositionService (which are
        // already set in the current reference frame).        
        Coords.Absolute coords_src = _PositionService.getAbsoluteCoords(animStart.relcoords);
        Coords.Absolute coords_dst = _PositionService.getLayeredArea(jcomp).abscoords_dst;          
        Coords.Absolute coords_interp = Coords.interpolate(frac, coords_src, coords_dst);   
        
        ((IHasComponent<?>) jcomp).getComponent().setAbsCoords(coords_interp);
        
        jcomp.validate();
    }
}