package gent.timdemey.cards.services.animation;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
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

    public AnimationService()
    {
        this.animTrackers = new ArrayList<>();
    }

    @PreloadOrder(order = PreloadOrderType.ISOLATED)
    @Override
    public void preload()
    {
        start();
    }

    @Override
    public void animate(List<? extends JComponent> jcomps, IPanelManager pm, AnimationKind kind)
    {
        if (jcomps.isEmpty())
        {
            return;
        }

        for (JComponent jcomp : jcomps)
        {
            if (!(jcomp instanceof IHasComponent<?>))
            {
                throw new IllegalArgumentException("To animate a component, it needs to implement IHasComponent, to discover what animation is needed for it");
            }
        }

        IPositionService posServ = Services.get(IPositionService.class);
        IAnimationDescriptorFactory animDescFact = Services.get(IAnimationDescriptorFactory.class);

        // sort from low to high in their card stack, so higher cards get higher layers
        List<JComponent> sorted = new ArrayList<>(jcomps);
        sorted.sort(Comparator.comparingInt(AnimationService::getStackOrder));

        int layer = getFirstFreeLayer(pm, posServ.getLayerRange(kind), sorted.size());

        for (JComponent jcomp : sorted)
        {
            IComponent comp = ((IHasComponent<?>) jcomp).getComponent();
            AnimationDescriptor descr = animDescFact.getAnimationDescriptor(comp);

            // to support animation while resizing the window, we need to save the relative coordinates
            // of the component at the start of the animation
            Coords.Absolute abscoords = comp.getAbsCoords();
            Coords.Relative relcoords = posServ.getRelativeCoords(abscoords);
            LayeredArea layeredArea = posServ.getLayeredArea(jcomp);

            // a component can only be animated once at a time
            stopAnimate(jcomp);

            AnimationTracker tracker = new AnimationTracker(jcomp, pm, descr, relcoords, layeredArea);
            animTrackers.add(tracker);

            // assign the component to a dedicated layer
            JSLayeredPane parent = (JSLayeredPane) jcomp.getParent();
            parent.setLayer(jcomp, layer++);

            // must tick immediately to have the component updated so it doesn't show
            // in wrong colors, wrong location, etc.
            long currTickTime = System.currentTimeMillis();
            tick(tracker, currTickTime);
        }
    }

    private static int getStackOrder(JComponent jcomp)
    {
        Object payload = ((IHasComponent<?>) jcomp).getComponent().getPayload();
        if (payload instanceof ReadOnlyCard card)
        {
            return card.getCardIndex();
        }
        return 0;
    }

    private int getFirstFreeLayer(IPanelManager pm, LayerRange range, int count)
    {
        Optional<Integer> maxBusyLayer = animTrackers.stream()
                .filter(at -> at.panelMan == pm)
                .map(c -> pm.getPanel().getLayer((Component) c.component))
                .filter(layer -> range.layerStart <= layer && layer <= range.layerEnd)
                .max(Integer::compare);

        int layer = maxBusyLayer.map(l -> l + 1).orElse(range.layerStart);
        if (layer + count - 1 > range.layerEnd)
        {
            layer = range.layerStart;
        }
        return layer;
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