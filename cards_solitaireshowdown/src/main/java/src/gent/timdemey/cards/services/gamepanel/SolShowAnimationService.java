package gent.timdemey.cards.services.gamepanel;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import gent.timdemey.cards.services.contract.AnimationDescriptor;
import gent.timdemey.cards.services.contract.ForegroundColorAnimation;
import gent.timdemey.cards.services.contract.IAnimation;
import gent.timdemey.cards.services.contract.MovingAnimation;
import gent.timdemey.cards.services.gamepanel.animations.AnimationEnd;
import gent.timdemey.cards.services.scaling.IScalableComponent;
import gent.timdemey.cards.services.scaling.comps.CardScoreScalableTextComponent;

public class SolShowAnimationService extends AnimationService
{
    private static final int TIME_CARDSCORE = 1200;
    private static final Color COLOR_CARDSCORE_START = new Color(255, 69, 0, 255);
    private static final Color COLOR_CARDSCORE_END = new Color(255, 69, 0, 0);

    @Override
    public AnimationDescriptor getAnimationDescriptor(IScalableComponent comp)
    {
        if (comp instanceof CardScoreScalableTextComponent)
        {            
            MovingAnimation anim1 = new MovingAnimation();
            ForegroundColorAnimation anim2 = new ForegroundColorAnimation(COLOR_CARDSCORE_START, COLOR_CARDSCORE_END);
            List<IAnimation> animations = Arrays.asList(anim1, anim2);
            
            AnimationDescriptor descr = new AnimationDescriptor(new AnimationEnd(true, -1), TIME_CARDSCORE, animations);
            return descr;
        }
        else 
        {
            return super.getAnimationDescriptor(comp);
        }       
    } 
}
