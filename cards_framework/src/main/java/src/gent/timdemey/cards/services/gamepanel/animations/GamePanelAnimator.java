package gent.timdemey.cards.services.gamepanel.animations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Timer;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.interfaces.IGamePanelService;
import gent.timdemey.cards.services.scaling.IScalableComponent;

public class GamePanelAnimator
{
    private final List<AnimationInfo> animationInfos;
    private final Timer timer;

    public GamePanelAnimator()
    {
        animationInfos = new ArrayList<>();
        timer = new Timer(10, e -> tick());
    }

    public void animate(IScalableComponent<?> component, AnimationEnd end, int animationTime, IAnimation... animations)
    {
        AnimationInfo animationInfo = new AnimationInfo(component, end, animationTime, System.currentTimeMillis(),
                animations);
        animationInfos.add(animationInfo);
    }

    public void start()
    {
        timer.start();
    }

    public void stop()
    {
        timer.stop();
    }

    private void tick()
    {
        long currTickTime = System.currentTimeMillis();

        Iterator<AnimationInfo> i = animationInfos.iterator();
        while (i.hasNext())
        {
            AnimationInfo animInfo = i.next();

            long dt = currTickTime - animInfo.tickStart;
            double frac = Math.min(1.0, 1.0 * dt / animInfo.animationTime);

            for (IAnimation animator : animInfo.animations)
            {
                animator.tick(frac, animInfo.component);
            }

            if (frac == 1.0)
            {
                i.remove();

                IGamePanelService gpServ = Services.get(IGamePanelService.class);
                if (animInfo.end.dispose)
                {
                    gpServ.remove(animInfo.component);
                }
                else
                {
                    gpServ.setLayer(animInfo.component, animInfo.end.layer);
                }
            }
        }
    }
    
    /**
     * Get the components that are currently being animated.
     * @return
     */
    public List<IScalableComponent<?>> getScalableComponents()
    {
        List<IScalableComponent<?>> comps = new ArrayList<>();
        for (AnimationInfo info : animationInfos)
        {
            comps.add(info.component);
        }
        return comps;
    }
}
