package gent.timdemey.cards.services.gamepanel;

import java.util.Arrays;
import java.util.List;

import gent.timdemey.cards.services.contract.AnimationDescriptor;
import gent.timdemey.cards.services.contract.IAnimation;
import gent.timdemey.cards.services.contract.MovingAnimation;
import gent.timdemey.cards.services.gamepanel.animations.AnimationEnd;
import gent.timdemey.cards.services.interfaces.IAnimationService;
import gent.timdemey.cards.services.scaling.IScalableComponent;
import gent.timdemey.cards.services.scaling.comps.CardScalableImageComponent;

public class AnimationService implements IAnimationService
{
    private static final int ANIMATION_TIME_CARD = 80;

    @Override
    public AnimationDescriptor getAnimationDescriptor(IScalableComponent comp)
    {
        if (comp instanceof CardScalableImageComponent)
        {
            MovingAnimation anim1 = new MovingAnimation();
            List<IAnimation> animations = Arrays.asList(anim1);
            AnimationDescriptor descr = new AnimationDescriptor(new AnimationEnd(false, -1), ANIMATION_TIME_CARD, animations);
            return descr;
        }
        
        throw new UnsupportedOperationException("No animation support for component of type " + comp.getClass().getSimpleName());
    }
}
