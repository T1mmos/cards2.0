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
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.IScalingService;

public class PanelService implements IPanelService
{
    private Map<PanelDescriptor, IPanelManager> panelMgrs;
    private Map<DataPanelDescriptor<?, ?>, IDataPanelManager<?,?>> dataPanelMgrs;
    
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
    public final void rescaleResourcesAsync(Runnable callback)
    {
        IScalingService scaleServ = Services.get(IScalingService.class);
        List<RescaleRequest> requests = new ArrayList<>();
        foreach(panelMgrs, mgr -> mgr.createRescaleRequests(requests));
        scaleServ.rescaleAsync(requests, () -> 
        {
            SwingUtilities.invokeLater(callback);           
        });
    }

    @Override
    public void createScalableComponents()
    {
        foreach(panelMgrs, IPanelManager::createScalableComponents);
    }
    
    @Override
    public void positionScalableComponents()
    {
        foreach(panelMgrs, IPanelManager::positionScalableComponents); 
    }
    


    @Override
    public void repaintScalableComponents()
    {
        foreach(panelMgrs, IPanelManager::repaintScalableComponents); 
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
