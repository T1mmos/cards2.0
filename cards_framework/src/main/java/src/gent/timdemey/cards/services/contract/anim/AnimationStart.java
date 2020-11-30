package gent.timdemey.cards.services.contract.anim;

import gent.timdemey.cards.services.contract.Coords;
import gent.timdemey.cards.services.contract.Coords.Relative;

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
    
    public AnimationStart(long time, Coords.Relative coords)
    {
        this.time = time;
        this.relcoords = coords;
    }
}
