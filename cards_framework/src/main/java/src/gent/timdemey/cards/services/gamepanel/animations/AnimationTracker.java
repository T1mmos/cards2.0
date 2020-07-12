package gent.timdemey.cards.services.gamepanel.animations;

import gent.timdemey.cards.services.contract.AnimationDescriptor;
import gent.timdemey.cards.services.contract.AnimationStart;
import gent.timdemey.cards.services.contract.Coords;
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
     * Holds values at the start of the animation, that can be used for all kind of interpolations.
     */
    final AnimationStart animStart;
    
    AnimationTracker(IScalableComponent component, AnimationDescriptor descriptor, Coords coords)
    {
        this.component = component;
        this.descriptor = descriptor;
        
        this.animStart = new AnimationStart(System.currentTimeMillis(), coords);
    }    
}
