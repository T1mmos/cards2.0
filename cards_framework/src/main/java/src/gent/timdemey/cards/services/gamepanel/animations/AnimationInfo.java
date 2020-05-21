package gent.timdemey.cards.services.gamepanel.animations;

import javax.swing.JComponent;

class AnimationInfo 
{
    final JComponent component;
    final boolean dispose;
    final int animationTime;
    final long tickStart;
    final IAnimation[] animations;
    
    public AnimationInfo (JComponent comp, boolean dispose, int animationTime, long tickStart, IAnimation ...  animations)
    {
        this.animationTime = animationTime;
        this.dispose = dispose;
        this.tickStart = tickStart;
        this.component = comp;
        this.animations = animations;
    }
}
