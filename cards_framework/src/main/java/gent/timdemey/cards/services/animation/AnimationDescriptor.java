package gent.timdemey.cards.services.animation;

import java.util.List;

public class AnimationDescriptor
{
    public final int animationTime;
    public final List<IAnimation> animations;
    public final boolean dispose;
    
    public AnimationDescriptor (int animationTime, List<IAnimation> animations, boolean dispose)
    {
        this.animationTime = animationTime;
        this.animations = animations;
        this.dispose = dispose;
    }
}
