package gent.timdemey.cards.services.panels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.SwingUtilities;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.services.contract.descriptors.DataPanelDescriptor;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptor;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.contract.preload.PreloadOrder;
import gent.timdemey.cards.services.contract.preload.PreloadOrderType;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.IScalingService;

public class PanelService implements IPanelService
{
    private Map<PanelDescriptor, IPanelManager> panelMgrs;
    private Map<DataPanelDescriptor<?, ?>, IDataPanelManager<?,?>> dataPanelMgrs;
    
    private boolean firstRescale = true;
    
    public PanelService()
    {
    }

    @Override
    @PreloadOrder(order = PreloadOrderType.DEPENDENT)
    public void preload()
    {
        panelMgrs = new HashMap<>();
        panelMgrs.put(PanelDescriptors.GAME, new GamePanelManager());
        panelMgrs.put(PanelDescriptors.LOAD, new LoadPanelManager());
        panelMgrs.put(PanelDescriptors.MENU, new MenuPanelManager());
        
        dataPanelMgrs = new HashMap<>();
        // add...
        
        foreach(panelMgrs, IPanelManager::preload);
        foreach(dataPanelMgrs, IPanelManager::preload);
    }    
    
    private void foreach(Map<? extends PanelDescriptor, ? extends IPanelManager> panelMgrs, Consumer<IPanelManager> func)
    {
        for (IPanelManager panelMgr : panelMgrs.values())
        {
            func.accept(panelMgr);
        }
    }

    @Override
    public PanelDescriptor getDefaultPanelDescriptor()
    {
        return PanelDescriptors.MENU;
    }
    
    @Override
    public IPanelManager getPanelManager(PanelDescriptor panelDesc)
    {
        IPanelManager panelMgr = panelMgrs.get(panelDesc);
        return panelMgr;
    }    

    @Override
    public <IN, OUT> IDataPanelManager<IN, OUT> getPanelManager(DataPanelDescriptor<IN, OUT> panelDesc)
    {
        IDataPanelManager<IN, OUT> panelMgr = (IDataPanelManager<IN, OUT>) dataPanelMgrs.get(panelDesc);
        return panelMgr;
    }
    
    @Override
    public final void rescaleResourcesAsync()
    {
        IScalingService scaleServ = Services.get(IScalingService.class);
        List<RescaleRequest> requests = createRescaleRequests();
        scaleServ.rescaleAsync(requests, () -> 
        {
            SwingUtilities.invokeLater(() -> onResourcesRescaled());           
        });
    }
    
    private void onResourcesRescaled()
    {
        if (firstRescale)
        {
            foreach(panelMgrs, IPanelManager::createScalableComponents);  
            foreach(panelMgrs, IPanelManager::positionScalableComponents);  
             
            firstRescale = false;      
        }
            
        IFrameService frameServ = Services.get(IFrameService.class);
        frameServ.hidePanel(PanelDescriptors.LOAD);
        

        foreach(panelMgrs, IPanelManager::onResourcesRescaled);     
    }

    protected List<RescaleRequest> createRescaleRequests()
    {
        List<RescaleRequest> requests = new ArrayList<>();

        foreach(panelMgrs, mgr -> mgr.createRescaleRequests(requests));
       
        return requests;
    }

    @Override
    public void relayout()
    {
        foreach(panelMgrs, IPanelManager::relayout); 
    }

    @Override
    public List<PanelDescriptor> getPanelDescriptors()
    {
        List<PanelDescriptor> allPanelMgrs = new ArrayList<>();
        allPanelMgrs.addAll(panelMgrs.keySet());
        allPanelMgrs.addAll(dataPanelMgrs.keySet());
        return allPanelMgrs;
    }
}
