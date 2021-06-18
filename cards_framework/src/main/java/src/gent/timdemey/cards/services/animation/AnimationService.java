package gent.timdemey.cards.services.animation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.contract.Coords;
import gent.timdemey.cards.services.contract.LayeredArea;
import gent.timdemey.cards.services.contract.preload.PreloadOrder;
import gent.timdemey.cards.services.contract.preload.PreloadOrderType;
import gent.timdemey.cards.services.interfaces.IAnimationDescriptorFactory;
import gent.timdemey.cards.services.interfaces.IAnimationService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.ui.components.ext.IComponent;
import gent.timdemey.cards.ui.components.swing.JSLayeredPane;
import gent.timdemey.cards.ui.panels.IPanelManager;

public class AnimationService implements IAnimationService
{

    private final List<AnimationTracker> animTrackers;
    private Timer timer = null;

    public AnimationService()
    {
        this.animTrackers = new ArrayList<>();
    }

    @PreloadOrder(order = PreloadOrderType.ISOLATED)
    public void preload()
    {
        start();
    }

    @Override
    public void animate(IComponent component)
    {
        IPositionService posServ = Services.get(IPositionService.class);
        IAnimationDescriptorFactory animDescFact = Services.get(IAnimationDescriptorFactory.class);

        AnimationDescriptor descr = animDescFact.getAnimationDescriptor(component);
        Coords.Relative relcoords = posServ.getRelativeCoords(component.getAbsCoords());

        AnimationTracker tracker = new AnimationTracker(component.getJComponent(), descr, relcoords);
        animTrackers.add(tracker);

        // must tick immediately to have the component updated so it doesn't show
        // in wrong colors, wrong location, etc.
        long currTickTime = System.currentTimeMillis();
        tick(tracker, currTickTime);
    }

    @Override
    public void animate(JSLayeredPane pb)
    {

    }

    public void stopAnimate(IComponent scaleComp)
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

    private void start()
    {
        timer = new Timer("Animation Timer", true);
        timer.schedule(new AnimationTask(), 0, 15);
    }

    private class AnimationTask extends TimerTask
    {
        @Override
        public void run()
        {
            SwingUtilities.invokeLater(() -> tick());
        }
    }

    public void stop()
    {
        timer.cancel();
        timer = null;
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

        IPositionService posServ = Services.get(IPositionService.class);
        IPanelManager panelMan = Services.get(IPanelManager.class);
        
        long dt = currTickTime - animTracker.animStart.time;
        double frac = Math.min(1.0, 1.0 * dt / animTracker.animDescriptor.animationTime);

        for (IAnimation animation : animTracker.animDescriptor.animations)
        {
            animation.tick(animTracker.component, frac, animTracker.animStart);
        }

        if (frac == 1.0)
        {
            if (animTracker.animDescriptor.dispose)
            {
                IPanelManager x;

                panelMan.remove(animTracker.component);
            }
            else
            {
                LayeredArea layArea = posServ.getEndLayeredArea(animTracker.component);

                panelMan.setLayer(animTracker.component, layArea.layer);
            }

            return true;
        }

        return false;
    }

    /**
     * Get the comp2jcomp that are currently being animated.
     * 
     * @return
     */
    public List<IComponent> getScalableComponents()
    {
        List<IComponent> comps = new ArrayList<>();
        for (AnimationTracker tracker : animTrackers)
        {
            comps.add(tracker.component);
        }
        return comps;
    }
}
