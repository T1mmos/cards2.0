package gent.timdemey.cards.services.interfaces;

import gent.timdemey.cards.services.contract.AnimationDescriptor;
import gent.timdemey.cards.services.scaling.IScalableComponent;

public interface IAnimationService
{
    /**
     * Returns an object that describes the animation a scalable component
     * should go through.
     * @param comp
     * @return
     */
    public AnimationDescriptor getAnimationDescriptor (IScalableComponent comp);
}
