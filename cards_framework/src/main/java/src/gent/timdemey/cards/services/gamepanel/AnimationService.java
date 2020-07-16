package gent.timdemey.cards.services.gamepanel;

import java.util.Arrays;
import java.util.List;

import gent.timdemey.cards.services.contract.AnimationDescriptor;
import gent.timdemey.cards.services.contract.IAnimation;
import gent.timdemey.cards.services.contract.MovingAnimation;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.services.interfaces.IAnimationService;
import gent.timdemey.cards.services.scaling.IScalableComponent;

public class AnimationService implements IAnimationService
{
    private static final int ANIMATION_TIME_CARD = 80;

    @Override
    public AnimationDescriptor getAnimationDescriptor(IScalableComponent comp)
    {
        if (comp.getComponentDescriptor().type == ComponentType.Card)
        {
            MovingAnimation anim1 = new MovingAnimation();
            List<IAnimation> animations = Arrays.asList(anim1);
            
            AnimationDescriptor descr = new AnimationDescriptor(ANIMATION_TIME_CARD, animations, false);
            return descr;
        }
        
        throw new UnsupportedOperationException("No animation support for component of type " + comp.getClass().getSimpleName());
    }
}
