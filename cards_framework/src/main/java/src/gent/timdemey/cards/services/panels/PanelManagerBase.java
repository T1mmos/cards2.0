package gent.timdemey.cards.services.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.contract.LayeredArea;
import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.services.contract.res.FontResource;
import gent.timdemey.cards.services.contract.res.ImageResource;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.interfaces.IResourceService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.services.scaling.IScalableComponent;
import gent.timdemey.cards.services.scaling.IScalableResource;
import gent.timdemey.cards.services.scaling.img.ScalableImageResource;
import gent.timdemey.cards.services.scaling.text.ScalableFontResource;

public abstract class PanelManagerBase implements IPanelManager
{
    @Override
    public boolean isCreated()
    {
        return get() != null;
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
    public void add(IScalableComponent comp)
    {
        get().add(comp.getComponent());
        position(comp);
    }

    @Override
    public void remove(IScalableComponent comp)
    {
        get().remove(comp.getComponent());
        get().revalidate();
        get().repaint();
    }
    
    public int getLayer(IScalableComponent scalableComponent)
    {
        return get().getLayer((Component) scalableComponent.getComponent());        
    }

    public void setLayer(IScalableComponent component, int layerIndex)
    {
        get().setLayer(component.getComponent(), layerIndex); 
    }
    
    @Override
    public void positionScalableComponents()
    {
        IScalingService scaleServ = Services.get(IScalingService.class);
        for (IScalableComponent scaleComp : scaleServ.getComponents())
        {
            position(scaleComp);
        }
    }
    
    private void position(IScalableComponent comp)
    {
        IPositionService posServ = Services.get(IPositionService.class);
        LayeredArea layArea = posServ.getStartLayeredArea(comp);
        comp.setCoords(layArea.coords);
        comp.setMirror(layArea.mirror);
        setLayer(comp, layArea.layer);
    }

    @Override
    public void repaintScalableComponents()
    {
        get().repaint();
    }
    
    @Override
    public void startAnimation(IScalableComponent scaleComp)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void stopAnimation(IScalableComponent scaleComp)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateComponent(IScalableComponent comp)
    {
        // TODO Auto-generated method stub
        
    }
    
    protected final void preloadImage(UUID resId, String path)
    {
        IResourceService resServ = Services.get(IResourceService.class);
        IScalingService scaleCompServ = Services.get(IScalingService.class);

        ImageResource imgRes = resServ.getImage(path);
        IScalableResource<?> scaleRes_back = new ScalableImageResource(resId, imgRes);
        scaleCompServ.addScalableResource(scaleRes_back);
    }

    protected final void preloadFont(UUID resId, String path)
    {
        IResourceService resServ = Services.get(IResourceService.class);
        IScalingService scaleCompServ = Services.get(IScalingService.class);

        FontResource resp_font = resServ.getFont(path);

        IScalableResource<?> scaleRes_font = new ScalableFontResource(resId, resp_font);
        scaleCompServ.addScalableResource(scaleRes_font);
    }
    
    protected final void addRescaleRequest(List<? super RescaleRequest> requests, ComponentType compType, UUID resId)
    {
        IPositionService posServ = Services.get(IPositionService.class);
        IScalingService scaleServ = Services.get(IScalingService.class);

        // get dimension
        Dimension dim = posServ.getResourceDimension(compType);

        // get the resource to rescale
        IScalableResource<?> res = scaleServ.getScalableResource(resId);

        // add rescale request for resource / dimension combination
        RescaleRequest rescReq = new RescaleRequest(res, dim);
        requests.add(rescReq);
    }
    
    @Override
    public void createRescaleRequests(List<? super RescaleRequest> requests)
    {
        
    }
    
    @Override
    public void createScalableComponents()
    {
        
    }
}
