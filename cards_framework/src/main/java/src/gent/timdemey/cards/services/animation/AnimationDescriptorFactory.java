package gent.timdemey.cards.services.animation;

import java.util.Arrays;
import java.util.List;

import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.interfaces.IAnimationDescriptorFactory;
import gent.timdemey.cards.ui.components.ext.IComponent;

public class AnimationDescriptorFactory implements IAnimationDescriptorFactory
{
    @Override
    public AnimationDescriptor getAnimationDescriptor(IComponent comp)
    {
        ComponentType compType = comp.getComponentType();
        if (compType == ComponentTypes.PANEL_MENU)
        {
            MovingAnimation anim1 = new MovingAnimation();
            List<IAnimation> animations = Arrays.asList(anim1);
            
            AnimationDescriptor descr = new AnimationDescriptor(1000, animations, false);
            return descr;
        }
        
        throw new UnsupportedOperationException("No animation support for JSLayeredPane with ComponentType " + compType);
    }
}
