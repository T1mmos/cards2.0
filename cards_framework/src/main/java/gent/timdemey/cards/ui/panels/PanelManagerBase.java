package gent.timdemey.cards.ui.panels;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JComponent;

import gent.timdemey.cards.readonlymodel.ReadOnlyEntityBase;
import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.services.contract.res.FontResource;
import gent.timdemey.cards.services.contract.res.ImageResource;
import gent.timdemey.cards.services.interfaces.IAnimationService;
import gent.timdemey.cards.services.interfaces.IIdService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.interfaces.IResourceCacheService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.ui.components.ISResource;
import gent.timdemey.cards.ui.components.SFontResource;
import gent.timdemey.cards.ui.components.SImageResource;
import gent.timdemey.cards.ui.components.ext.IComponent;
import gent.timdemey.cards.ui.components.ext.IHasComponent;
import gent.timdemey.cards.ui.components.swing.JSFactory;
import gent.timdemey.cards.ui.components.swing.JSImage;
import gent.timdemey.cards.ui.components.swing.JSLabel;

public abstract class PanelManagerBase implements IPanelManager
{
    protected final Map<UUID, JComponent> comp2jcomp;
    protected final Map<UUID, UUID> entity2comp;
    
    private IResourceCacheService _ResourceCacheService;
    private IScalingService _ScalingService;
    private IIdService _IdService;
    private IPositionService _PositionService;
    private IAnimationService _AnimationService;
    
    public PanelManagerBase(
            IAnimationService animationService,
            IResourceCacheService resourceCacheService,
            IScalingService scalingService,
            IIdService idService,
            IPositionService positionService)
    {
        this._AnimationService = animationService;
        this._ResourceCacheService = resourceCacheService;
        this._ScalingService = scalingService;
        this._IdService = idService;
        this._PositionService = positionService;
        
        
        this.comp2jcomp = new HashMap<>();
        this.entity2comp = new HashMap<>();
    }

    @Override
    public void destroyPanel()
    {
        comp2jcomp.clear();
        entity2comp.clear();
    }

    @Override
    public JComponent getComponentById(UUID compId)
    {
        JComponent jcomp = comp2jcomp.get(compId);
        if (jcomp == null)
        {
            throw new IllegalStateException("No component was found in this Panel Manager mapped onto id '"+compId+"'");
        }
        
        return jcomp;        
    }
    
    @Override
    public JComponent getComponentByEntityId(UUID entityId)
    {
        UUID compId = entity2comp.get(entityId);
        if (compId == null)
        {
            throw new IllegalArgumentException("EntityId '"+entityId+"' is not mapped to a IHasComponent, so a JComponent cannot be found for it");
        }
        return getComponentById(compId);
    }
    
    protected final JSImage createJSImage(UUID compId, ComponentType compType, Object payload, SImageResource ... imgResources)
    {
        JSImage jsimage = JSFactory.createImageScaled(compId, compType, imgResources); 
        initAndAddComponent(jsimage, payload);
        return jsimage;
    }
    
    protected final JSLabel createJSLabel(UUID compId, ComponentType compType, String text, Object payload, SFontResource fontRes)
    {
        JSLabel jslabel = JSFactory.createLabelScaled(compId, text, compType, fontRes);
        initAndAddComponent(jslabel, payload);
        return jslabel;
    }
    
    protected void initAndAddComponent(IHasComponent<?> hasComp, Object payload)
    {
        IComponent comp = hasComp.getComponent();
        UUID compid = comp.getId();
        
        // should not exist yet
        JComponent existing = comp2jcomp.get(compid);
        if (existing != null)
        {
            throw new IllegalArgumentException("A component already exists for the given id: " + compid);            
        }
        
        // init
        comp.setPanelManager(this);
        comp.setPayload(payload);
        
        // get JComponent, sync to domain model
        JComponent jcomp = comp.getJComponent();
        updateComponent(jcomp);
        
        // add to lookup
        comp2jcomp.put(compid, jcomp);
        
        // add component to panel
        getPanel().add(jcomp);
    }
    
    @Override
    public JComponent getComponent(ReadOnlyEntityBase<?> entity)
    {
        return getComponentByEntityId(entity.getId());
    }
    
    protected UUID createComponentId(ReadOnlyEntityBase<?> entity)
    {     
        UUID compId = entity2comp.get(entity.getId());
        if (compId != null)
        {
            throw new IllegalStateException("ComponentId "+entity.getId()+" shouldn't exist yet for entity: " + entity);
        }

        // get the ids
        compId = _IdService.createScalableComponentId(entity);
        entity2comp.put(entity.getId(), compId);
        
        return compId;
    }
    
    @Override
    public boolean isPanelCreated()
    {
        return getPanel() != null;
    }
        
    @Override
    public void onShown()
    {
        
    }
    
    @Override
    public void onHidden()
    {
    }
        
    @Override
    public void positionComponents()
    {
        for (JComponent comp : getComponents())
        {
            positionComponent(comp);
        }
    }
    
    @Override
    public void positionComponent(JComponent comp)
    {
        // by default, do nothing, as in most cases a LayoutManager is responsible for positioning
    }
    
    protected final void preloadImage(UUID resId, String path)
    {
        ImageResource imgRes = _ResourceCacheService.getImage(path);
        ISResource<?> scaleRes_back = new SImageResource(resId, imgRes);
        _ScalingService.addSResource(scaleRes_back);
    }

    protected final void preloadFont(UUID resId, String path)
    {
        FontResource resp_font = _ResourceCacheService.getFont(path);

        ISResource<?> scaleRes_font = new SFontResource(resId, resp_font);
        _ScalingService.addSResource(scaleRes_font);
    }
    
    protected final void addRescaleRequest(List<? super RescaleRequest> requests, ComponentType compType, UUID resId)
    {
        // get dimension
        Dimension dim = _PositionService.getResourceDimension(compType);

        // get the resource to rescale
        ISResource<?> res = _ScalingService.getSResource(resId);

        // add rescale request for resource / dimension combination
        RescaleRequest rescReq = new RescaleRequest(res, dim);
        requests.add(rescReq);
    }
    
    @Override
    public void addRescaleRequests(List<? super RescaleRequest> requests)
    {
    }
    
    @Override
    public void addComponentCreators(List<Runnable> runnables)
    {
        // by default, there is no async component creation
    }

    @Override
    public void startAnimate(JComponent jcomp)
    {
        _AnimationService.animate(jcomp, this);        
    }

    @Override
    public void stopAnimate(JComponent jcomp)
    {
        _AnimationService.stopAnimate(jcomp);
    }
    
    @Override
    public void updateComponent(JComponent comp)
    {
        // most panels do not sync towards a domain model, so base implementation does nothing
    }
    
    @Override
    public JComponent getComponentAt(Point p)
    {        
        JComponent jcomp = (JComponent) getPanel().getComponentAt(p);
        return jcomp;
    }
    
    private Stream<JComponent> getComponentsStream()
    {
        return Arrays.stream(getPanel().getComponents()).map(c -> (JComponent) c);
    }
    
    private Stream<JComponent> getComponentsAtStream(Point p)
    {
        return getComponentsStream()
            .filter(s -> s.getBounds().contains(p))
            .filter(s -> filterPanelManager((IHasComponent<?>) s));
    }
    
    private Stream<JComponent> getComponentsInStream(Rectangle rect)
    {
        return getComponentsStream()
            .filter(s -> s.getBounds().intersects(rect))
            .filter(s -> filterPanelManager((IHasComponent<?>) s));
    }
    
    private boolean filterPanelManager(IHasComponent<?> s)
    {
        IPanelManager panelMan = s.getComponent().getPanelManager();
        return panelMan != null && panelMan.isPanelCreated() && panelMan.getPanel().isVisible();
    }

    @Override
    public List<JComponent> getComponentsAt(Point p)
    {
        return getComponentsAtStream(p).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends JComponent> List<T> getComponentsAt(Point p, Class<T> clazz)
    {
        return getComponentsAtStream(p)
            .filter(s -> clazz.isAssignableFrom(s.getClass()))
            .map(s -> (T) s)
            .collect(Collectors.toList());
    }

    @Override
    public List<JComponent> getComponentsIn(Rectangle rect)
    {
        return getComponentsInStream(rect).collect(Collectors.toList());
    }
    
    @Override
    public List<JComponent> getComponents()
    {
        return getComponentsStream().collect(Collectors.toList());
    }
}
