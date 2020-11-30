package gent.timdemey.cards.services.panels;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import gent.timdemey.cards.services.contract.anim.AnimationDescriptor;
import gent.timdemey.cards.services.contract.anim.BorderColorAnimation;
import gent.timdemey.cards.services.contract.anim.ForegroundColorAnimation;
import gent.timdemey.cards.services.contract.anim.IAnimation;
import gent.timdemey.cards.services.contract.anim.MovingAnimation;
import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.panels.AnimationService;
import gent.timdemey.cards.services.resources.SolShowResourceDefines;
import gent.timdemey.cards.services.scaling.IScalableComponent;

public class SolShowAnimationService extends AnimationService
{
    @Override
    public AnimationDescriptor getAnimationDescriptor(IScalableComponent comp)
    {
        if (comp.getComponentType().hasTypeName(ComponentTypes.CARDSCORE))
        {            
            MovingAnimation anim1 = new MovingAnimation();
            
            Color color_inner_start = SolShowResourceDefines.COLOR_ANIMATION_CARDSCORE_INNER_START;
            Color color_inner_end = SolShowResourceDefines.COLOR_ANIMATION_CARDSCORE_INNER_END;
            Color color_outer_start = SolShowResourceDefines.COLOR_ANIMATION_CARDSCORE_OUTER_START;
            Color color_outer_end = SolShowResourceDefines.COLOR_ANIMATION_CARDSCORE_OUTER_END;
            
            ForegroundColorAnimation anim2 = new ForegroundColorAnimation(color_inner_start, color_inner_end);
            BorderColorAnimation anim3 = new BorderColorAnimation(color_outer_start, color_outer_end);
            List<IAnimation> animations = Arrays.asList(anim1, anim2, anim3);
            
            AnimationDescriptor descr = new AnimationDescriptor(SolShowResourceDefines.TIME_MS_ANIMATION_CARDSCORE, animations, true);
            return descr;
        }
        else 
        {
            return super.getAnimationDescriptor(comp);
        }       
    } 
}
