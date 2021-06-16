package gent.timdemey.cards.services.animation;

import javax.swing.JComponent;

import gent.timdemey.cards.services.contract.Coords;

/**
 * Bundles all information for animating a component. 
 * @author timdm
 */
public class AnimationTracker
{
    /**
     * The component being animated.
     */
    final JComponent component;
    
    /**
     * Describes the entire animation.
     */
    final AnimationDescriptor animDescriptor;
    
    /**
     * Holds values at the start of the animation, that can be used for all kind of interpolations.
     */
    final AnimationStart animStart;
    
    AnimationTracker(JComponent component, AnimationDescriptor animDescriptor, Coords.Relative relcoords)
    {
        this.component = component;
        this.animDescriptor = animDescriptor;
        this.animStart = new AnimationStart(System.currentTimeMillis(), relcoords);
    }    
}
