package gent.timdemey.cards.services.gamepanel.animations;

import gent.timdemey.cards.services.contract.AnimationDescriptor;
import gent.timdemey.cards.services.scaling.IScalableComponent;

class AnimationTracker
{
    /**
     * The component being animated.
     */
    final IScalableComponent component;
    
    /**
     * Describes the animation.
     */
    final AnimationDescriptor descriptor;
    
    /**
     * Time when the animation started.
     */
    final long start_time;
    
    AnimationTracker(IScalableComponent component, AnimationDescriptor descriptor)
    {
        this.component = component;
        this.descriptor = descriptor;
        this.start_time = System.currentTimeMillis();
    }    
}
