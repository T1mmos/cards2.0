package gent.timdemey.cards.services.scaleman;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
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
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.services.contract.LayeredArea;
import gent.timdemey.cards.services.interfaces.IGamePanelService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.interfaces.IScalableComponentService;
import gent.timdemey.cards.services.interfaces.IUUIDService;
import gent.timdemey.cards.services.scaleman.comps.CardScalableImageComponent;
import gent.timdemey.cards.services.scaleman.img.ScalableImageResource;

public class ScalableComponentService implements IScalableComponentService
{
    private final Executor barrierExecutor;
    private final Executor taskExecutor;

    private final Map<UUID, IScalableResource> resources;
    private final Map<UUID, IScalableComponent> components;

    private final Map<UUID, UUID> model2comp;

    /**
     * Produces threads used by the barrier executor which waits for all tasks to
     * complete via CompletionFuture.allOf().
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
     * Produces threads used by the task executor which reads and scales buffered
     * images.
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

    public ScalableComponentService()
    {
        this.barrierExecutor = Executors.newFixedThreadPool(1, new BarrierThreadFactory());
        this.taskExecutor = new ThreadPoolExecutor(1, 5, 5L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ScalableImageTaskThreadFactory());
        this.resources = new HashMap<>();
        this.components = new HashMap<>();
        this.model2comp = new HashMap<>();
    }

    @Override
    public void rescaleAllResources(Runnable callback)
    {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());

        List<CompletableFuture<?>> futures = new ArrayList<CompletableFuture<?>>();

        // find out the necessary scales of all resources by looking at the
        // current dimensions of all scaled components and build a list of
        // unique tasks to execute (we don't scale a resource into the same
        // dimensions twice, yet a resource may be requested in multiple
        // scales).
        Set<ScalableResourceRescaleTask> rescaleTasks = new HashSet<>();
        for (IScalableComponent scaleComp : components.values())
        {
            Rectangle bounds = scaleComp.getBounds();
            int width = bounds.width;
            int height = bounds.height;
            for (IScalableResource scaleRes : scaleComp.getResources())
            {
                ScalableResourceRescaleTask task = new ScalableResourceRescaleTask(scaleRes, width, height);
                rescaleTasks.add(task);
            }
        }

        // put the tasks into futures to execute
        for (ScalableResourceRescaleTask task : rescaleTasks)
        {
            CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() -> task, taskExecutor).thenAcceptAsync(t -> t.scaleResource.rescale(task.width,
                task.height), taskExecutor);
            futures.add(cf);
        }

        // execute all futures and invoke callback when all is complete
        CompletableFuture<?>[] arr_futures = new CompletableFuture<?>[futures.size()];
        futures.toArray(arr_futures);
        CompletableFuture.allOf(arr_futures).thenAcceptAsync((nil) ->
        {
            if(callback != null)
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
    public void addScalableResource(IScalableResource scaleRes)
    {
        resources.put(scaleRes.getId(), scaleRes);
    }

    @Override
    public IScalableComponent getOrCreate(ReadOnlyCard card)
    {
        IUUIDService uuidServ = Services.get(IUUIDService.class);

        UUID compId = model2comp.get(card.getId());
        if(compId == null)
        {
            // get the ids
            compId = uuidServ.createCardComponentId(card);
        }

        CardScalableImageComponent comp = (CardScalableImageComponent) components.get(compId);
        if(comp == null)
        {
            UUID resFrontId = uuidServ.createCardFrontResourceId(card.getSuit(), card.getValue());
            UUID resBackId = uuidServ.createCardBackResourceId();

            // create the component using its necessary image resources
            ScalableImageResource res_front = (ScalableImageResource) resources.get(resFrontId);
            ScalableImageResource res_back = (ScalableImageResource) resources.get(resBackId);
            comp = new CardScalableImageComponent(compId, card, res_front, res_back);

            // initialize the card to show its front or back
            comp.updateVisible();

            components.put(compId, comp);
        }

        return comp;
    }

    @Override
    public void setAllBounds()
    {
        IPositionService posServ = Services.get(IPositionService.class);

        // apply all bounds on scalable components
        for (IScalableComponent scaleComp : components.values())
        {
            LayeredArea layRect = posServ.getLayeredArea(scaleComp, false);
            scaleComp.setBounds(layRect.getBounds2D());
        }
    }

    @Override
    public IScalableComponent getComponentAt(Point p)
    {
        IGamePanelService gpServ = Services.get(IGamePanelService.class);
        Iterator<IScalableComponent> it = components.values().stream().sorted((x,y) -> 
        { 
            return gpServ.getLayer(y) - gpServ.getLayer(x);
        }).iterator();
        while(it.hasNext())
        {
            IScalableComponent scaleComp = it.next();
            scaleComp.
        }
        
    }

    @Override
    public List<IScalableComponent> getComponentsAt(Point p)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends IScalableComponent> List<T> getComponentsAt(Point p, Class<T> clazz)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
