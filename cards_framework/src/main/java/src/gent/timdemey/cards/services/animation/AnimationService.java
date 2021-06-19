package gent.timdemey.cards.services.animation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;
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
import gent.timdemey.cards.ui.components.ext.IHasComponent;
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
    public void animate(JComponent jcomp)
    {
        if (!(jcomp instanceof IHasComponent<?>))
        {
            throw new IllegalArgumentException("To animate a component, it needs to implement IHasComponent, to discover what animation is needed for it");
        }
        IHasComponent<?> hasComp = (IHasComponent<?>) jcomp;        
        IComponent comp = hasComp.getComponent();
        
        IPositionService posServ = Services.get(IPositionService.class);
        IAnimationDescriptorFactory animDescFact = Services.get(IAnimationDescriptorFactory.class);

        AnimationDescriptor descr = animDescFact.getAnimationDescriptor(comp);
        
        // to support animation while resizing the window, we need to save the relative coordinates
        // of the component at the start of the animation
        Coords.Absolute abscoords = ((IHasComponent<?>) jcomp).getComponent().getAbsCoords();
        Coords.Relative relcoords = posServ.getRelativeCoords(abscoords);

        AnimationTracker tracker = new AnimationTracker(jcomp, descr, relcoords);
        animTrackers.add(tracker);

        // must tick immediately to have the component updated so it doesn't show
        // in wrong colors, wrong location, etc.
        long currTickTime = System.currentTimeMillis();
        tick(tracker, currTickTime);
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
        IPanelManager pm = Services.get(IPanelManager.class);
        
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
                pm.getPanel().remove(animTracker.component);
            }
            else
            {
                LayeredArea layArea = posServ.getEndLayeredArea(animTracker.component);

                pm.getPanel().setLayer(animTracker.component, layArea.layer);
            }

            return true;
        }

        return false;
    }
}
