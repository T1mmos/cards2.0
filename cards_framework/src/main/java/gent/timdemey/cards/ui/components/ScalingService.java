package gent.timdemey.cards.ui.components;

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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.services.interfaces.IScalingService;

public final class ScalingService implements IScalingService
{
    private final Executor barrierExecutor;
    private final Executor taskExecutor;

    protected final Map<UUID, ISResource<?>> resources;

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
    private static class SImageTaskThreadFactory implements ThreadFactory
    {
        private static int count = 0;

        @Override
        public Thread newThread(Runnable r)
        {
            Thread thr =  new Thread(r, "Scalable Image Task Slave #" + count++);
            thr.setDaemon(true);
            return thr;
        }
    }

    public ScalingService()
    {
        this.barrierExecutor = Executors.newFixedThreadPool(1, new BarrierThreadFactory());
        this.taskExecutor = new ThreadPoolExecutor(1, 52, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new SImageTaskThreadFactory());
        this.resources = new HashMap<>();
    }

    @Override
    public void rescaleAsync(List<RescaleRequest> requests, Runnable callback)
    {
        // the rescale requests might need access to the game domain object, only accessible
        // from the EDT - the rescale itself is offloaded on a worker thread
        if (!SwingUtilities.isEventDispatchThread())
        {
            throw new IllegalStateException("This method must be called from the EDT");
        }
        
        List<CompletableFuture<?>> futures = new ArrayList<CompletableFuture<?>>();

        // find out the necessary scales of all resources by looking at the
        // current dimensions of all scaled comp2jcomp and build a list of
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
    public void clearResourceCache()
    {
        resources.clear();
    }


    @Override
    public void addSResource(ISResource<?> scaleRes)
    {
        resources.put(scaleRes.getId(), scaleRes);
    }
    
    @Override
    public ISResource<?> getSResource(UUID resId)
    {
        ISResource<?> res = resources.get(resId);
        if (res == null)
        {
            String msg = "No resource with id=%s was found, ensure all resources are preloaded before creating comp2jcomp that use them";
            String format = String.format(msg, resId);
            throw new NullPointerException(format);
        }
        return res;
    }
    
  
}
