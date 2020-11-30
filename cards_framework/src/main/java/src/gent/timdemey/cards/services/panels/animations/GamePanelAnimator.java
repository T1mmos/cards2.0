package gent.timdemey.cards.services.panels.animations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Timer;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.contract.Coords;
import gent.timdemey.cards.services.contract.LayeredArea;
import gent.timdemey.cards.services.contract.anim.AnimationDescriptor;
import gent.timdemey.cards.services.contract.anim.IAnimation;
import gent.timdemey.cards.services.interfaces.IAnimationService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.scaling.IScalableComponent;

public class GamePanelAnimator
{
    private final List<AnimationTracker> animTrackers;
    private final Timer timer;

    public GamePanelAnimator()
    {
        animTrackers = new ArrayList<>();
        timer = new Timer(10, e -> tick());
    }

    public void animate(IScalableComponent component)
    {
        IAnimationService animServ = Services.get(IAnimationService.class);
        IPositionService posServ = Services.get(IPositionService.class);
        
        AnimationDescriptor descr = animServ.getAnimationDescriptor(component);        
        Coords.Relative relcoords = posServ.getRelativeCoords(component.getCoords());        
        
        AnimationTracker tracker = new AnimationTracker(component, descr, relcoords);
        animTrackers.add(tracker);        
    }

    public void stopAnimate(IScalableComponent scaleComp)
    {
        Iterator<AnimationTracker> it = animTrackers.iterator();
        while (it.hasNext())
        {
            AnimationTracker ai = it.next();
            if (ai.component == scaleComp)
            {
                it.remove();
                break;
            }
        }
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
        
        Iterator<AnimationTracker> i = animTrackers.iterator();
        while (i.hasNext())
        {
            AnimationTracker animTracker = i.next();

            long dt = currTickTime - animTracker.animStart.time;
            double frac = Math.min(1.0, 1.0 * dt / animTracker.descriptor.animationTime);

            for (IAnimation animation : animTracker.descriptor.animations)
            {
                animation.tick(animTracker.component, frac, animTracker.animStart);
            }

            if (frac == 1.0)
            {
                i.remove();

                IPanelService pServ = Services.get(IPanelService.class);
                if (animTracker.descriptor.dispose)
                {
                    pServ.remove(animTracker.component);
                }
                else
                {

                    IPositionService posServ = Services.get(IPositionService.class);
                    LayeredArea layArea = posServ.getEndLayeredArea(animTracker.component);
                    
                    pServ.setLayer(animTracker.component, layArea.layer);
                }
            }
        }
    }

    /**
     * Get the components that are currently being animated.
     * 
     * @return
     */
    public List<IScalableComponent> getScalableComponents()
    {
        List<IScalableComponent> comps = new ArrayList<>();
        for (AnimationTracker tracker : animTrackers)
        {
            comps.add(tracker.component);
        }
        return comps;
    }
}
