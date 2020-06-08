package gent.timdemey.cards.services.scaleman;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

import gent.timdemey.cards.services.interfaces.IScalableComponentService;

public class ScalableComponentService implements IScalableComponentService
{
    private final Executor barrierExecutor;
    private final Executor taskExecutor;

    private final Map<UUID, IScalableResource> resources;
    private final Map<UUID, IScalableComponent> components;

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
        this.taskExecutor = new ThreadPoolExecutor(1, 5, 5L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
            new ScalableImageTaskThreadFactory());
        this.resources = new HashMap<>();
        this.components = new HashMap<>();
        
    }
    
    @Override
    public void updateResources(Runnable callback)
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
            CompletableFuture<Void> cf = CompletableFuture
                    .supplyAsync(() -> task, taskExecutor)
                    .thenAcceptAsync(t -> t.scaleResource.rescale(task.width, task.height), taskExecutor);
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
    public void updateComponents()
    {
        // it may be more efficient to call an update on the game panel itself i.o. 
        // updating all components individually -> todo
        SwingUtilities.invokeLater(() ->
        {
            for (IScalableComponent scaleComp : components.values())
            {
                scaleComp.update();
            }
        });
    }


    @Override
    public void clearManagedObjects()
    {
        resources.clear();
        components.clear();
    }

    @Override
    public IScalableComponent getScalableComponent(UUID id)
    {
        return components.get(id);        
    }

    @Override
    public void addScalableResource(IScalableResource scaleRes)
    {
        resources.put(scaleRes.getId(), scaleRes);
    }

    @Override
    public void addScalableComponent(IScalableComponent scaleComp)
    {
        components.put(scaleComp.getId(), scaleComp);
    }

    @Override
    public IScalableResource getScalableResource(UUID id)
    {
        return resources.get(id);
    }
}
