package gent.timdemey.cards.services.scaling;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.swing.SwingUtilities;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.services.interfaces.IGamePanelService;
import gent.timdemey.cards.services.interfaces.IIdService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.services.scaling.comps.CardScalableImageComponent;
import gent.timdemey.cards.services.scaling.comps.CardStackScalableImageComponent;
import gent.timdemey.cards.services.scaling.img.ScalableImageResource;

public final class ScalingService implements IScalingService
{
    private final Executor barrierExecutor;
    private final Executor taskExecutor;

    protected final Map<UUID, IScalableResource<?>> resources;
    protected final Map<UUID, IScalableComponent> components;

    protected final Map<UUID, UUID> model2comp;

    /**
     * Produces threads used by the barrier executor which waits for all tasks
     * to complete via CompletionFuture.allOf().
     */
    private static class BarrierThreadFactory implements ThreadFactory
    {
        private static int count = 0;

        @Override
        public Thread newThread(Runnable r)
        {
            Thread thr = new Thread(r, "Scalable Image Task Barrier Thread #" + count++);
            thr.setDaemon(true);
            return thr;
        }
    }

    /**
     * Produces threads used by the task executor which reads and scales
     * buffered images.
     */
    private static class ScalableImageTaskThreadFactory implements ThreadFactory
    {
        private static int count = 0;

        @Override
        public Thread newThread(Runnable r)
        {
            return new Thread(r, "Scalable Image Task Slave #" + count++);
        }
    }

    public ScalingService()
    {
        this.barrierExecutor = Executors.newFixedThreadPool(1, new BarrierThreadFactory());
        this.taskExecutor = new ThreadPoolExecutor(1, 52, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new ScalableImageTaskThreadFactory());
        this.resources = new HashMap<>();
        this.components = new HashMap<>();
        this.model2comp = new HashMap<>();
    }

    @Override
    public void rescaleAsync(List<RescaleRequest> requests, Runnable callback)
    {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());
        List<CompletableFuture<?>> futures = new ArrayList<CompletableFuture<?>>();

        // find out the necessary scales of all resources by looking at the
        // current dimensions of all scaled components and build a list of
        // unique tasks to execute (we don't scale a resource into the same
        // dimensions twice, yet a resource may be requested in multiple
        // scales).
        Set<RescaleRequest> uniqueRequests = new HashSet<>(requests);

        // put the tasks into futures to execute
        for (RescaleRequest request : uniqueRequests)
        {
            CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() -> request, taskExecutor)
                    .thenAcceptAsync(t -> t.scalableResource.rescale(t.dimension), taskExecutor);
            futures.add(cf);
        }

        // execute all futures and invoke callback when all is complete
        CompletableFuture<?>[] arr_futures = new CompletableFuture<?>[futures.size()];
        futures.toArray(arr_futures);
        CompletableFuture.allOf(arr_futures).thenAcceptAsync((nil) -> {
            if (callback != null)
            {
                callback.run();
            }
        }, barrierExecutor);
    }

    @Override
    public void clearManagedObjects()
    {
        resources.clear();
        components.clear();
        model2comp.clear();
    }

    @Override
    public IScalableComponent getScalableComponent(UUID compId)
    {
        return components.get(compId);
    }

    @Override
    public void addScalableResource(IScalableResource<?> scaleRes)
    {
        resources.put(scaleRes.getId(), scaleRes);
    }

    @Override
    public void addScalableComponent(IScalableComponent scaleComp) 
    {
        components.put(scaleComp.getId(), scaleComp);
    };
    
    @Override
    public IScalableResource<?> getScalableResource(UUID resId)
    {
        IScalableResource<?> res = resources.get(resId);
        if (res == null)
        {
            String msg = "No resource with id=%s was found, ensure all resources are preloaded before creating components that use them";
            String format = String.format(msg, resId);
            throw new NullPointerException(format);
        }
        return res;
    }
    
    @Override
    public IScalableComponent getComponentAt(Point p)
    {
        IGamePanelService gpServ = Services.get(IGamePanelService.class);
        Iterator<IScalableComponent> it = components.values().stream().sorted((x, y) -> {
            return gpServ.getLayer(y) - gpServ.getLayer(x);
        }).iterator();
        while (it.hasNext())
        {
            IScalableComponent scaleComp = it.next();
            if (scaleComp.getBounds().contains(p))
            {
                return scaleComp;
            }
        }
        return null;
    }

    @Override
    public List<IScalableComponent> getComponentsAt(Point p)
    {
        return components.values().stream().filter(s -> s.getBounds().contains(p)).collect(Collectors.toList());
    }

    @Override
    public <T extends IScalableComponent> List<T> getComponentsAt(Point p, Class<T> clazz)
    {
        return components.values().stream().filter(s -> s.getBounds().contains(p) && clazz.isAssignableFrom(s.getClass())).map(s -> (T) s)
                .collect(Collectors.toList());
    }

    @Override
    public List<IScalableComponent> getComponentsIn(Rectangle rect)
    {
        return components.values().stream().filter(s -> s.getBounds().intersects(rect)).collect(Collectors.toList());
    }
    
    @Override
    public List<IScalableComponent> getComponents()
    {
        return new ArrayList<>(components.values());
    }

    @Override
    public IScalableComponent getOrCreateScalableComponent(ReadOnlyCard card)
    {
        IIdService uuidServ = Services.get(IIdService.class);

        UUID compId = model2comp.get(card.getId());
        if (compId == null)
        {
            // get the ids
            compId = uuidServ.createCardScalableComponentId(card);
        }

        CardScalableImageComponent comp = (CardScalableImageComponent) components.get(compId);
        if (comp == null)
        {
            UUID resFrontId = uuidServ.createCardFrontScalableResourceId(card.getSuit(), card.getValue());
            UUID resBackId = uuidServ.createCardBackScalableResourceId();

            // create the component using its necessary image resources
            ScalableImageResource res_front = (ScalableImageResource) getScalableResource(resFrontId);
            ScalableImageResource res_back = (ScalableImageResource) getScalableResource(resBackId);
            comp = new CardScalableImageComponent(compId, card, res_front, res_back);

            components.put(compId, comp);
        }

        return comp;
    }
    
    @Override
    public IScalableComponent getOrCreateScalableComponent(ReadOnlyCardStack cardstack)
    {
        IIdService idServ = Services.get(IIdService.class);

        UUID compId = model2comp.get(cardstack.getId());
        if (compId == null)
        {
            // get the ids
            compId = idServ.createCardStackScalableComponentId(cardstack);
        }

        CardStackScalableImageComponent comp = (CardStackScalableImageComponent) components.get(compId);
        if (comp == null)
        {
            UUID csResId = idServ.createCardStackScalableResourceId(cardstack.getCardStackType());

            // create the component using its necessary image resources
            ScalableImageResource res = (ScalableImageResource) getScalableResource(csResId);
            comp = new CardStackScalableImageComponent(compId, cardstack, res);

            components.put(compId, comp);
        }

        return comp;
    }
}
