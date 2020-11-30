package gent.timdemey.cards.services.contract.anim;

import gent.timdemey.cards.services.scaling.IScalableComponent;

public interface IAnimation
{
    /**
     * Updates the state of the given component according to the time fraction indicating 
     * at what point in time the animation currently is. The fraction is a real number
     * in the range [0.0 ... 1.0].
     * @param comp the component being animated
     * @param frac the fraction of the total animation time that already passed, useful for interpolation
     * @param animStart values taken at the moment when the animation started, useful for interpolation
     */
    public void tick (IScalableComponent comp, double frac, AnimationStart animStart);
}
