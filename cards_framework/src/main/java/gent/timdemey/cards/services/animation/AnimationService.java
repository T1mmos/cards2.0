package gent.timdemey.cards.services.animation;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;


import gent.timdemey.cards.services.contract.Coords;
import gent.timdemey.cards.services.contract.LayeredArea;
import gent.timdemey.cards.services.contract.preload.IPreload;
import gent.timdemey.cards.services.contract.preload.PreloadOrder;
import gent.timdemey.cards.services.contract.preload.PreloadOrderType;
import gent.timdemey.cards.services.interfaces.IAnimationDescriptorFactory;
import gent.timdemey.cards.services.interfaces.IAnimationService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.ui.components.ext.IComponent;
import gent.timdemey.cards.ui.components.ext.IHasComponent;
import gent.timdemey.cards.ui.components.swing.JSLayeredPane;
import gent.timdemey.cards.ui.panels.IPanelManager;

public class AnimationService implements IAnimationService, IPreload
{
    private final List<AnimationTracker> animTrackers;
    private Timer timer = null;
    private final IPositionService _PositionService;
    private final IAnimationDescriptorFactory _AnimationDescriptorFactory;

    public AnimationService(
        IPositionService positionService,
        IAnimationDescriptorFactory animationDescriptorFactory) 
    {
        this._PositionService = positionService;
        this._AnimationDescriptorFactory = animationDescriptorFactory;
        this.animTrackers = new ArrayList<>();
    }

    @PreloadOrder(order = PreloadOrderType.ISOLATED)
    @Override
    public void preload()
    {
        start();
    }

    @Override
    public void animate(JComponent jcomp, IPanelManager pm)
    {
        if (!(jcomp instanceof IHasComponent<?>))
        {
            throw new IllegalArgumentException("To animate a component, it needs to implement IHasComponent, to discover what animation is needed for it");
        }
        
        IHasComponent<?> hasComp = (IHasComponent<?>) jcomp;        
        IComponent comp = hasComp.getComponent();
        
        AnimationDescriptor descr = _AnimationDescriptorFactory.getAnimationDescriptor(comp);
        
        // to support animation while resizing the window, we need to save the relative coordinates
        // of the component at the start of the animation
        Coords.Absolute abscoords = ((IHasComponent<?>) jcomp).getComponent().getAbsCoords();
        Coords.Relative relcoords = _PositionService.getRelativeCoords(abscoords);
        LayeredArea layeredArea = _PositionService.getLayeredArea(jcomp);

        AnimationTracker tracker = new AnimationTracker(jcomp, pm, descr, relcoords, layeredArea);
        animTrackers.add(tracker);

        // assign the component to a dedicated layer
        setAnimationLayer(jcomp, pm, layeredArea);
        
        // must tick immediately to have the component updated so it doesn't show
        // in wrong colors, wrong location, etc.
        long currTickTime = System.currentTimeMillis();
        tick(tracker, currTickTime);
    }
        
    private void setAnimationLayer(JComponent jcomp, IPanelManager pm, LayeredArea layeredArea)
    {        
        LayerRange range = layeredArea.startLayerRange;
        
        Optional<Integer> maxBusyLayer = animTrackers.stream()
                .filter(at -> at.panelMan == pm)
                .map(c -> pm.getPanel().getLayer((Component) c.component))
                .filter(layer -> range.layerStart <= layer && layer <= range.layerEnd)
                .max(Integer::compare);
               
        int layer;
        if (maxBusyLayer.isPresent())
        {
            layer = maxBusyLayer.get() + 1;
        }
        else
        {
            layer = range.layerStart;
        }
        
        JSLayeredPane parent = (JSLayeredPane) jcomp.getParent();
        parent.setLayer(jcomp, layer);
    }

    @Override
    public void stopAnimate(JComponent jcomp)
    {
        Iterator<AnimationTracker> it = animTrackers.iterator();
        while (it.hasNext())
        {
            AnimationTracker ai = it.next();
            if (ai.component == jcomp)
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
        long dt = currTickTime - animTracker.animStart.time;
        double frac = Math.min(1.0, 1.0 * dt / animTracker.animDescriptor.animationTime);

        for (IAnimation animation : animTracker.animDescriptor.animations)
        {
            animation.tick(animTracker.component, frac, animTracker.animStart);
        }

        if (frac == 1.0)
        {
            JSLayeredPane parent = animTracker.panelMan.getPanel();
            if (animTracker.animDescriptor.dispose)
            {
                parent.remove(animTracker.component);
            }
            else
            {
                parent.setLayer(animTracker.component, animTracker.animStart.layeredArea.endLayer);
            }

            return true;
        }

        return false;
    }
}