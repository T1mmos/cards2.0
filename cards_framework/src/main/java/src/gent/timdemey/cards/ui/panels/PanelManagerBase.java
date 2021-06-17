package gent.timdemey.cards.ui.panels;

import java.awt.Dimension;
import java.util.List;
import java.util.UUID;

import javax.swing.JComponent;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.contract.LayeredArea;
import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.services.contract.res.FontResource;
import gent.timdemey.cards.services.contract.res.ImageResource;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.interfaces.IResourceCacheService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.ui.components.ISResource;
import gent.timdemey.cards.ui.components.SFontResource;
import gent.timdemey.cards.ui.components.SImageResource;
import gent.timdemey.cards.ui.components.ext.IComponent;

public abstract class PanelManagerBase implements IPanelManager
{
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
        IScalingService scaleServ = Services.get(IScalingService.class);
        for (IComponent scaleComp : scaleServ.getComponents())
        {
            position(scaleComp);
        }
    }
    
    private void position(JComponent comp)
    {
        
        IPositionService posServ = Services.get(IPositionService.class);
        LayeredArea layArea = posServ.getStartLayeredArea(comp);
        comp.setCoords(layArea.coords);
        comp.getdrawer().setMirror(layArea.mirror);
        setLayer(comp, layArea.layer);
    }

    @Override
    public void startAnimation(IComponent scaleComp)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void stopAnimation(IComponent scaleComp)
    {
        // TODO Auto-generated method stub
        
    }

    protected final void preloadImage(UUID resId, String path)
    {
        IResourceCacheService resServ = Services.get(IResourceCacheService.class);
        IScalingService scaleCompServ = Services.get(IScalingService.class);

        ImageResource imgRes = resServ.getImage(path);
        ISResource<?> scaleRes_back = new SImageResource(resId, imgRes);
        scaleCompServ.addSResource(scaleRes_back);
    }

    protected final void preloadFont(UUID resId, String path)
    {
        IResourceCacheService resServ = Services.get(IResourceCacheService.class);
        IScalingService scaleCompServ = Services.get(IScalingService.class);

        FontResource resp_font = resServ.getFont(path);

        ISResource<?> scaleRes_font = new SFontResource(resId, resp_font);
        scaleCompServ.addSResource(scaleRes_font);
    }
    
    protected final void addRescaleRequest(List<? super RescaleRequest> requests, ComponentType compType, UUID resId)
    {
        IPositionService posServ = Services.get(IPositionService.class);
        IScalingService scaleServ = Services.get(IScalingService.class);

        // get dimension
        Dimension dim = posServ.getResourceDimension(compType);

        // get the resource to rescale
        ISResource<?> res = scaleServ.getSResource(resId);

        // add rescale request for resource / dimension combination
        RescaleRequest rescReq = new RescaleRequest(res, dim);
        requests.add(rescReq);
    }
    
    @Override
    public void createRescaleRequests(List<? super RescaleRequest> requests)
    {
        
    }
    
    @Override
    public void createComponents()
    {
        
    }
}
