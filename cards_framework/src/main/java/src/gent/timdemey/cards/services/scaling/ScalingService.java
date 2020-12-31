package gent.timdemey.cards.services.scaling;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import java.util.stream.Stream;

import javax.swing.SwingUtilities;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyEntityBase;
import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.interfaces.IIdService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.services.panels.IPanelManager;
import gent.timdemey.cards.services.scaling.img.ScalableImageComponent;
import gent.timdemey.cards.services.scaling.img.ScalableImageResource;
import gent.timdemey.cards.services.scaling.text.ScalableFontResource;
import gent.timdemey.cards.services.scaling.text.ScalableTextComponent;

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
            Thread thr =  new Thread(r, "Scalable Image Task Slave #" + count++);
            //thr.setDaemon(true);
            return thr;
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
        // the rescale requests might need access to the game domain object, only accessible
        // from the EDT - the rescale itself is offloaded on a worker thread
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
    public void clearComponentCache()
    {
        components.clear();
        model2comp.clear();
    }
    
    @Override 
    public void clearResourceCache()
    {
        if (components.size() > 0 || model2comp.size() > 0)
        {
            throw new IllegalStateException("Resources cache can only be cleared if no components exist that may be using them");
        }
        
        resources.clear();
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
        Optional<IScalableComponent> comp = getComponentsAtStream(p)
            .sorted((x, y) -> y.getPanelManager().getLayer(y) - x.getPanelManager().getLayer(x))
            .findFirst();
       
        return comp.isEmpty() ? null : comp.get();
    }
    
    private Stream<IScalableComponent> getComponentsAtStream(Point p)
    {
        return components.values().stream()
            .filter(s -> s.getCoords().getBounds().contains(p))
            .filter(this::filterPanelManager);
    }
    
    private Stream<IScalableComponent> getComponentsInStream(Rectangle rect)
    {
        return components.values().stream()
            .filter(s -> s.getCoords().getBounds().intersects(rect))
            .filter(this::filterPanelManager);
    }
    
    private boolean filterPanelManager(IScalableComponent s)
    {
        return s.getPanelManager() != null && s.getPanelManager().isCreated() && s.getPanelManager().get().isVisible();
    }

    @Override
    public List<IScalableComponent> getComponentsAt(Point p)
    {
        return getComponentsAtStream(p).collect(Collectors.toList());
    }

    @Override
    public <T extends IScalableComponent> List<T> getComponentsAt(Point p, Class<T> clazz)
    {
        return getComponentsAtStream(p)
            .filter(s -> clazz.isAssignableFrom(s.getClass())).map(s -> (T) s)
            .collect(Collectors.toList());
    }

    @Override
    public List<IScalableComponent> getComponentsIn(Rectangle rect)
    {
        return getComponentsInStream(rect).collect(Collectors.toList());
    }
    
    @Override
    public List<IScalableComponent> getComponents()
    {
        return new ArrayList<>(components.values());
    }

    @Override
    public IScalableComponent createScalableComponent(ReadOnlyCard card, IPanelManager panelManager)
    {
        IIdService uuidServ = Services.get(IIdService.class);

        UUID compId = createComponentId(card);

        IScalableComponent comp = components.get(compId);
        if (comp != null)
        {
            throw new IllegalArgumentException("A scalable component already exist for the given model object: " + card);            
        }
        
        UUID resFrontId = uuidServ.createCardFrontScalableResourceId(card.getSuit(), card.getValue());
        UUID resBackId = uuidServ.createCardBackScalableResourceId();

        // create the component using its necessary image resources
        ScalableImageResource res_front = (ScalableImageResource) getScalableResource(resFrontId);
        ScalableImageResource res_back = (ScalableImageResource) getScalableResource(resBackId);
        
        return createScalableImageComponent(compId, ComponentTypes.CARD, panelManager, card, res_front, res_back);
    }
    
    @Override
    public final ScalableImageComponent createScalableImageComponent(UUID compId, ComponentType compType, IPanelManager panelMgr, Object payload, ScalableImageResource ... imgResources)
    {
        ScalableImageComponent comp = new ScalableImageComponent(compId, compType, imgResources);
        comp.setPanelManager(panelMgr);
        comp.setPayload(payload);

        if (imgResources.length == 1)
        {
            comp.setScalableImageResource(imgResources[0].id);
        }
        
        components.put(compId, comp);
        return comp;
    }


    @Override
    public ScalableTextComponent createScalableTextComponent(UUID compId, ComponentType compType, String text, IPanelManager panelMgr, Object payload, ScalableFontResource fontRes)
    {
        ScalableTextComponent comp = new ScalableTextComponent(compId, text, compType, fontRes);
        comp.setPanelManager(panelMgr);
        comp.setPayload(payload);
        return comp;
    }
    
    @Override
    public IScalableComponent getScalableComponent(ReadOnlyCard card)
    {
        return getScalableComponent((ReadOnlyEntityBase<?>) card);
    }
    
    @Override
    public IScalableComponent createScalableComponent(ReadOnlyCardStack cardstack, IPanelManager panelManager)
    {
        IIdService idServ = Services.get(IIdService.class);

        UUID compId = createComponentId(cardstack);

        IScalableComponent comp = components.get(compId);
        if (comp != null)
        {
            throw new IllegalArgumentException("A scalable component already exist for the given model object: " + cardstack);            
        }
        
        UUID csResId = idServ.createCardStackScalableResourceId(cardstack.getCardStackType());

        // create the component using its necessary image resources
        ScalableImageResource res = (ScalableImageResource) getScalableResource(csResId);
        return createScalableImageComponent(compId, ComponentTypes.CARDSTACK, panelManager, cardstack, res);
    }
    
    @Override
    public IScalableComponent getScalableComponent(ReadOnlyCardStack cardStack)
    {
        return getScalableComponent((ReadOnlyEntityBase<?>) cardStack);
    }
    
    private IScalableComponent getScalableComponent(ReadOnlyEntityBase<?> entity)
    {
        UUID compId = model2comp.get(entity.getId());
        if (compId == null)
        {
            throw new IllegalArgumentException("A scalable component ID does not exist for the given model object: " + entity);      
        }
        IScalableComponent comp = components.get(compId);
        if (comp == null)
        {
            throw new IllegalArgumentException("A scalable component does not exist for the given model object: " + entity);    
        }
        
        return comp;
    }
    
    private UUID createComponentId(ReadOnlyEntityBase<?> entity)
    {     
        IIdService idServ = Services.get(IIdService.class);
        UUID compId = model2comp.get(entity.getId());
        if (compId != null)
        {
            throw new IllegalStateException("ComponentId "+entity.getId()+" shouldn't exist yet for entity: " + entity);
        }

        // get the ids
        compId = idServ.createScalableComponentId(entity);
        model2comp.put(entity.getId(), compId);
        
        return compId;
    }
}
