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
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.panels.IPanelManager;
import gent.timdemey.cards.services.scaling.IScalableComponent;

public class PanelAnimator
{
    private final List<AnimationTracker> animTrackers;
    private final Timer timer;
    private final IPanelManager panelManager;

    public PanelAnimator(IPanelManager panelManager)
    {
        this.animTrackers = new ArrayList<>();
        this.timer = new Timer(10, e -> tick());
        this.panelManager = panelManager;
    }

    public void animate(IScalableComponent component)
    {
        IAnimationService animServ = Services.get(IAnimationService.class);
        IPositionService posServ = Services.get(IPositionService.class);
        
        AnimationDescriptor descr = animServ.getAnimationDescriptor(component);        
        Coords.Relative relcoords = posServ.getRelativeCoords(component.getCoords());        
        
        AnimationTracker tracker = new AnimationTracker(component, descr, relcoords);
        animTrackers.add(tracker);
        
        // must tick immediately to have the component updated so it doesn't show
        // in wrong colors, wrong location, etc.
        long currTickTime = System.currentTimeMillis();
        tick(tracker, currTickTime);
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
            boolean ended = tick(animTracker, currTickTime);
            if (ended) 
            {
                i.remove();
            }
        }
    }

    private boolean tick(AnimationTracker animTracker, long currTickTime)
    {
        long dt = currTickTime - animTracker.animStart.time;
        double frac = Math.min(1.0, 1.0 * dt / animTracker.descriptor.animationTime);

        for (IAnimation animation : animTracker.descriptor.animations)
        {
            animation.tick(animTracker.component, frac, animTracker.animStart);
        }

        if (frac == 1.0)
        {
            if (animTracker.descriptor.dispose)
            {
                panelManager.remove(animTracker.component);
            }
            else
            {

                IPositionService posServ = Services.get(IPositionService.class);
                LayeredArea layArea = posServ.getEndLayeredArea(animTracker.component);
                
                panelManager.setLayer(animTracker.component, layArea.layer);
            }
            
            return true;
        }
        
        return false;
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
