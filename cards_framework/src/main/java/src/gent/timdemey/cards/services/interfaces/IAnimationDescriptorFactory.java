package gent.timdemey.cards.services.interfaces;

import gent.timdemey.cards.services.animation.AnimationDescriptor;
import gent.timdemey.cards.ui.components.ISComponent;
import gent.timdemey.cards.ui.components.JSLayeredPane;

public interface IAnimationDescriptorFactory
{
    AnimationDescriptor getAnimationDescriptor(ISComponent comp);
    
    AnimationDescriptor getAnimationDescriptor(JSLayeredPane pb);
}
