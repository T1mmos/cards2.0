package gent.timdemey.cards.services.contract;

public class AnimationStart
{
    /**
     * Time when the animation started.
     */
    public final long time;
    
    /**
     * Coordinates of the component when the animation started.
     */
    public final Coords coords;
    
    public AnimationStart(long time, Coords coords)
    {
        this.time = time;
        this.coords = coords;
    }
}
