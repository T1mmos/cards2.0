package gent.timdemey.cards.services.gamepanel.animations;

import javax.swing.JComponent;

class AnimationInfo 
{
    final JComponent component;
    final AnimationEnd end;
    final int animationTime;
    final long tickStart;
    final IAnimation[] animations;
    
    public AnimationInfo (JComponent comp, AnimationEnd end, int animationTime, long tickStart, IAnimation ...  animations)
    {
        this.animationTime = animationTime;
        this.end = end;
        this.tickStart = tickStart;
        this.component = comp;
        this.animations = animations;
    }
}
