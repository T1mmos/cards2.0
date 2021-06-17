package gent.timdemey.cards.services.interfaces;

import gent.timdemey.cards.services.animation.AnimationDescriptor;
import gent.timdemey.cards.ui.components.ext.IComponent;
import gent.timdemey.cards.ui.components.swing.JSLayeredPane;

public interface IAnimationDescriptorFactory
{
    AnimationDescriptor getAnimationDescriptor(IComponent comp);
    
    AnimationDescriptor getAnimationDescriptor(JSLayeredPane pb);
}
