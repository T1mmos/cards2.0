package gent.timdemey.cards.services.contract;

import gent.timdemey.cards.services.scaling.IScalableComponent;

public interface IAnimation
{
    /**
     * Updates the state of the given component according to the time fraction indicating 
     * at what point in time the animation currently is. The fraction is a real number
     * in the range [0.0 ... 1.0].
     * @param frac
     * @param comp
     */
    public void tick (double frac, IScalableComponent comp);
}
