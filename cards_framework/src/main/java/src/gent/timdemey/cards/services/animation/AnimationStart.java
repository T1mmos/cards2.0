package gent.timdemey.cards.services.animation;

import gent.timdemey.cards.services.contract.Coords;
import gent.timdemey.cards.services.contract.LayeredArea;

public class AnimationStart
{
    /**
     * Time when the animation started.
     */
    public final long time;
    
    /**
     * Relative coordinates of the component when the animation started.
     */
    public final Coords.Relative relcoords;
    
    /**
     * Holds absolute positioning and layer info.
     */
    final LayeredArea layeredArea;
    
    public AnimationStart(long time, Coords.Relative coords, LayeredArea layeredArea)
    {
        this.time = time;
        this.relcoords = coords;
        this.layeredArea = layeredArea;
    }
}
