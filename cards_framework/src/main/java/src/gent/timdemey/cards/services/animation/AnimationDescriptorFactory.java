package gent.timdemey.cards.services.animation;

import java.util.Arrays;
import java.util.List;

import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IAnimationDescriptorFactory;
import gent.timdemey.cards.ui.components.ISComponent;
import gent.timdemey.cards.ui.components.JSLayeredPane;

public class AnimationDescriptorFactory implements IAnimationDescriptorFactory
{
    public AnimationDescriptor getAnimationDescriptor(ISComponent comp)
    {
        if (comp.getComponentType().hasTypeName(ComponentTypes.CARD))
        {
            MovingAnimation anim1 = new MovingAnimation();
            List<IAnimation> animations = Arrays.asList(anim1);

            AnimationDescriptor descr = new AnimationDescriptor(80, animations, false);
            return descr;
        }
        
        throw new UnsupportedOperationException("No animation support for component of type " + comp.getClass().getSimpleName());
    }
    
    public AnimationDescriptor getAnimationDescriptor(JSLayeredPane pb)
    {
        if (pb.getComponentType() == PanelDescriptors.Menu)
        {
            MovingAnimation anim1 = new MovingAnimation();
            List<IAnimation> animations = Arrays.asList(anim1);
            
            AnimationDescriptor descr = new AnimationDescriptor(1000, animations, false);
            return descr;
        }
        
        throw new UnsupportedOperationException("No animation support for JSLayeredPane with id " + pb.getPanelName());
    }
}
