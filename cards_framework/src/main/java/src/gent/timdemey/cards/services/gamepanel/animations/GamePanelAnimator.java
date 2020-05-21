package gent.timdemey.cards.services.gamepanel.animations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.Timer;

public class GamePanelAnimator
{
    private final List<AnimationInfo> animationInfos;
    private final Timer timer;
    
    public GamePanelAnimator()
    {
        animationInfos = new ArrayList<>();
        timer = new Timer(15, e -> tick());
    }

    public void animate(JComponent comp, boolean dispose, int animationTime, IAnimation ...  animations)
    {
        AnimationInfo animationInfo = new AnimationInfo(comp, dispose, animationTime, System.currentTimeMillis(), animations);
        animationInfos.add(animationInfo);        
    }
    
    public void start()
    {
        timer.start();        
    }
    
    public void stop ()
    {
        timer.stop();
    }

    private void tick()
    {
        long currTickTime = System.currentTimeMillis();

        Iterator<AnimationInfo> i = animationInfos.iterator();
        while (i.hasNext()) {
            AnimationInfo animInfo = i.next();
            
            long dt = currTickTime - animInfo.tickStart;
            double frac = Math.min(1.0, 1.0 * dt / animInfo.animationTime);
            
            for(IAnimation animator : animInfo.animations)
            {
                animator.tick(frac, animInfo.component);
            }
            
            if (frac == 1.0)
            {
                i.remove();
                if (animInfo.dispose)
                {
                    animInfo.component.getParent().remove(animInfo.component);
                }
            }
        }
    }
}
