package gent.timdemey.cards.services.contract;

import java.util.List;

import gent.timdemey.cards.services.gamepanel.animations.AnimationEnd;

public class AnimationDescriptor
{
    public final AnimationEnd end;
    public final int animationTime;
    public final List<IAnimation> animations;
    
    public AnimationDescriptor (AnimationEnd end, int animationTime, List<IAnimation> animations)
    {
        this.animationTime = animationTime;
        this.end = end;
        this.animations = animations;
    }
}
