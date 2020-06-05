package gent.timdemey.cards.services.scaleman;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
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
import java.util.function.Consumer;

import javax.swing.SwingUtilities;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.services.IImageService;
import gent.timdemey.cards.services.IScalableComponentService;
import gent.timdemey.cards.services.scaleman.img.ScalableImageComponent;

public class ScalableComponentService implements IScalableComponentService
{
    private final Executor barrierExecutor;
    private final Executor taskExecutor;

    private final Map<String, Set<String>> groupMap;
    private final Map<String, BufferedImageInfo> imageMap;
    private final BiMap<UUID, ScalableImageComponent> componentMap;
    private final Map<UUID, String> pathMap;

    private volatile boolean error = false;

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

    private static class CreateTaskContext
    {
        private final ImageDefinition imgDef;
        private final BufferedImage image;

        private CreateTaskContext(ImageDefinition imgDef, BufferedImage image)
        {
            this.imgDef = imgDef;
            this.image = null;
        }

        private CreateTaskContext(CreateTaskContext other, BufferedImage image)
        {
            this.imgDef = other.imgDef;
            this.image = image;
        }
    }

    public ScalableComponentService()
    {
        this.barrierExecutor = Executors.newFixedThreadPool(1, new BarrierThreadFactory());
        this.taskExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 5L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
            new ScalableImageTaskThreadFactory());
        this.groupMap = new HashMap<>();
        this.imageMap = Collections.synchronizedMap(new HashMap<>());
        this.componentMap = HashBiMap.create();
        this.pathMap = new HashMap<>();
    }

    private void updateUI()
    {
        SwingUtilities.invokeLater(() ->
        {
            for (Object obj : componentMap.keySet())
            {
                ScalableImageComponent scalable = componentMap.get(obj);
                String path = pathMap.get(obj);
                BufferedImageInfo biInfo = imageMap.get(path);

                scalable.setImage(biInfo == null ? null : biInfo.currentImg, path);
                scalable.repaint();
            }
        });
    }

    @Override
    public ScalableImageComponent getScalableImage(UUID id)
    {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());
        Preconditions.checkNotNull(id);

        if(!componentMap.containsKey(id))
        {
            componentMap.put(id, new ScalableImageComponent(id));
        }

        return componentMap.get(id);
    }

    @Override
    public UUID getUUID(ScalableImageComponent scaleImg)
    {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());
        Preconditions.checkState(componentMap.containsValue(scaleImg));

        UUID id = componentMap.inverse().get(scaleImg);
        return id;
    }

    @Override
    public void loadImagesAsync(List<ImageDefinition> imgDefs, Consumer<Boolean> onResult)
    {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());
        Preconditions.checkArgument(imgDefs != null);

        // check that all objects are non-null
        for (int i = 0; i < imgDefs.size(); i++)
        {
            ImageDefinition def = imgDefs.get(i);
            if(def == null)
            {
                throw new IllegalArgumentException("null object at index " + i);
            }
        }

        // fill list of futures that all need to complete to fire the callback

        error = false;
        CompletableFuture<?>[] futures = new CompletableFuture<?>[imgDefs.size()];
        for (int i = 0; i < imgDefs.size(); i++)
        {
            ImageDefinition imgDef = imgDefs.get(i);

            CreateTaskContext context = new CreateTaskContext(imgDef, null);
            CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() -> context, taskExecutor) // input
                .thenApplyAsync(this::readImage, taskExecutor) // to buffered image
                .exceptionally(t ->
                {
                    error = true;
                    return onReadImageException(t, context);
                }).thenAccept(this::addBufferedImage); // add buffered image to lists
            futures[i] = cf;
        }

        if(onResult != null)
        {
            CompletableFuture.allOf(futures).thenRunAsync(() -> onResult.accept(!error), barrierExecutor);
        }
    }

    @Override
    public void setSize(String group, int x, int y)
    {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());
        Preconditions.checkArgument(groupMap.containsKey(group), "No such group: %s", group);
        Preconditions.checkArgument(x > 0);
        Preconditions.checkArgument(y > 0);

        for (String path : groupMap.get(group))
        {
            BufferedImageInfo biInfo = imageMap.get(path);
            biInfo.requested_x = x;
            biInfo.requested_y = y;
        }
    }

    @Override
    public void rescaleAsync(Runnable callback)
    {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());

        List<CompletableFuture<?>> futures = new ArrayList<CompletableFuture<?>>();

        for (String path : imageMap.keySet())
        {
            BufferedImageInfo biInfo = imageMap.get(path);

            int width = biInfo.requested_x;
            int height = biInfo.requested_y;

            BufferedImageScaler scaler = new BufferedImageScaler(biInfo.sourceImg, width, height);
            CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() -> scaler, taskExecutor).thenApplyAsync(_scaler -> _scaler.getScaledInstance(),
                taskExecutor).thenAccept(scaledImg -> biInfo.currentImg = scaledImg);

            futures.add(cf);
        }

        CompletableFuture<?>[] arr_futures = new CompletableFuture<?>[futures.size()];
        futures.toArray(arr_futures);
        CompletableFuture.allOf(arr_futures).thenAcceptAsync((nil) ->
        {
            // updateUI();
            if(callback != null)
            {
                callback.run();
            }
        }, barrierExecutor);
    }

    @Override
    public void setImage(UUID id, String path)
    {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());
        Preconditions.checkArgument(id != null);
        Preconditions.checkArgument(path != null);
        Preconditions.checkArgument(componentMap.containsKey(id));
        Preconditions.checkArgument(imageMap.containsKey(path));

        ScalableImageComponent scaleImg = componentMap.get(id);
        BufferedImageInfo biInfo = imageMap.get(path);

        pathMap.put(id, path);
        scaleImg.setImage(biInfo.currentImg, path);
        scaleImg.repaint();
    }

    private CreateTaskContext readImage(CreateTaskContext context)
    {
        Preconditions.checkState(!SwingUtilities.isEventDispatchThread());

        IImageService imgServ = Services.get(IImageService.class);
        BufferedImage img = imgServ.read(context.imgDef.path);

        return new CreateTaskContext(context, img);
    }

    private CreateTaskContext onReadImageException(Throwable t, CreateTaskContext context)
    {
        Preconditions.checkState(!SwingUtilities.isEventDispatchThread());
        Services.get(ILogManager.class).log(t);

        return new CreateTaskContext(context, null);
    }

    private void addBufferedImage(CreateTaskContext context)
    {
        Preconditions.checkState(!SwingUtilities.isEventDispatchThread());

        if(context.image != null && context.imgDef != null)
        {
            SwingUtilities.invokeLater(() ->
            {
                // add path to group
                if(!groupMap.containsKey(context.imgDef.group))
                {
                    groupMap.put(context.imgDef.group, new HashSet<>());
                }
                groupMap.get(context.imgDef.group).add(context.imgDef.path);

                // add info to imagemap
                BufferedImageInfo biInfo = new BufferedImageInfo();
                biInfo.sourceImg = context.image;
                biInfo.currentImg = context.image;
                biInfo.requested_x = context.image != null ? context.image.getWidth() : 0;
                biInfo.requested_y = context.image != null ? context.image.getHeight() : 0;

                imageMap.put(context.imgDef.path, biInfo);
            });
        }
    }

    @Override
    public void clearManagedObjects()
    {
        pathMap.clear();
        componentMap.clear();
    }
}
