package gent.timdemey.cards.services.panels.animations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLayeredPane;
import javax.swing.Timer;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.cards.Suit;
import gent.timdemey.cards.model.entities.cards.Value;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.services.contract.Coords;
import gent.timdemey.cards.services.contract.LayeredArea;
import gent.timdemey.cards.services.contract.anim.AnimationDescriptor;
import gent.timdemey.cards.services.contract.anim.IAnimation;
import gent.timdemey.cards.services.interfaces.IAnimationService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.scaling.IScalableComponent;
import gent.timdemey.cards.services.scaling.img.ScalableImageComponent;

public class PanelAnimator
{
    private final List<AnimationTracker> animTrackers;
    private final Timer timer;

    public PanelAnimator()
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
            if (animTracker.component instanceof ScalableImageComponent)
                {
                Object payload = animTracker.component.getPayload();
                if (payload instanceof ReadOnlyCard)
                {
                    ReadOnlyCard card = (ReadOnlyCard) payload;
                    if (card.getSuit() == Suit.HEARTS && card.getValue() == Value.V_A)
                    {
                        int lyr = ((JLayeredPane) animTracker.component.getComponent().getParent()).getLayer(animTracker.component.getComponent());
                        Logger.info("Card animation: anim=%s, frac=%s, layer=%s", animation.getClass().getSimpleName(), frac, lyr);
                    }
                    
                }
                
                }
           
        }

        if (frac == 1.0)
        {
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
