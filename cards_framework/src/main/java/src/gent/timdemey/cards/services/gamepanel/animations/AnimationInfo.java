package gent.timdemey.cards.services.gamepanel.animations;

import gent.timdemey.cards.services.scaling.IScalableComponent;

class AnimationInfo 
{
    final IScalableComponent<?> component;
    final AnimationEnd end;
    final int animationTime;
    final long tickStart;
    final IAnimation[] animations;
    
    public AnimationInfo (IScalableComponent<?> component, AnimationEnd end, int animationTime, long tickStart, IAnimation ...  animations)
    {
        this.animationTime = animationTime;
        this.end = end;
        this.tickStart = tickStart;
        this.component = component;
        this.animations = animations;
    }
}
