package gent.timdemey.cards.services.animation;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.resources.SolShowResourceDefines;
import gent.timdemey.cards.ui.components.ext.IComponent;

public class SolShowAnimationDescriptorFactory extends AnimationDescriptorFactory
{
    private static final int TIME_MS_ANIMATION_CARDSCORE = 500;
    private static final int TIME_MS_ANIMATION_CARD      = 200;
    
    @Override
    public AnimationDescriptor getAnimationDescriptor(IComponent comp)
    {      
        if (comp.getComponentType().hasTypeName(ComponentTypes.CARDSCORE))
        {            
            MovingAnimation anim1 = new MovingAnimation();
            
            Color color_inner_start = SolShowResourceDefines.COLOR_ANIMATION_CARDSCORE_INNER_START;
            Color color_inner_end = SolShowResourceDefines.COLOR_ANIMATION_CARDSCORE_INNER_END;
            Color color_outer_start = SolShowResourceDefines.COLOR_ANIMATION_CARDSCORE_OUTER_START;
            Color color_outer_end = SolShowResourceDefines.COLOR_ANIMATION_CARDSCORE_OUTER_END;
            
            ForegroundColorAnimation anim2 = new ForegroundColorAnimation(color_inner_start, color_inner_end);
            OuterColorAnimation anim3 = new OuterColorAnimation(color_outer_start, color_outer_end);
            AlphaAnimation anim4 = new AlphaAnimation(0.8f, 0.0f);
            List<IAnimation> animations = Arrays.asList(anim1, anim2, anim3, anim4);
            
            AnimationDescriptor descr = new AnimationDescriptor(TIME_MS_ANIMATION_CARDSCORE, animations, true);
            return descr;
        }
        else if (comp.getComponentType().hasTypeName(ComponentTypes.CARD))
        {
            MovingAnimation anim1 = new MovingAnimation();
            List<IAnimation> animations = Arrays.asList(anim1);
            AnimationDescriptor descr = new AnimationDescriptor(TIME_MS_ANIMATION_CARD, animations, false);
            return descr;
        }
        else 
        {
            return super.getAnimationDescriptor(comp);
        }       
    } 
}
