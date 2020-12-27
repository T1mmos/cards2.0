package gent.timdemey.cards.services.panels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
import gent.timdemey.cards.services.panels.game.GamePanelManager;
import gent.timdemey.cards.services.panels.load.LoadPanelManager;
import gent.timdemey.cards.services.panels.menu.MenuPanelManager;
import gent.timdemey.cards.services.panels.message.MessagePanelManager;
import gent.timdemey.cards.services.panels.mp.JoinMPGamePanelManager;
import gent.timdemey.cards.services.panels.mp.StartServerPanelManager;

public class PanelService implements IPanelService
{
    private Map<PanelDescriptor, IPanelManager> panelMgrs;
    
    public PanelService()
    {
    }

    @Override
    @PreloadOrder(order = PreloadOrderType.DEPENDENT)
    public void preload()
    {
        panelMgrs = new HashMap<>();
        addAbsentPanelManagers();        
    }
    
    protected void addAbsentPanelManagers()
    {
        addPanelManagerIfAbsent(PanelDescriptors.GAME, () -> new GamePanelManager());
        addPanelManagerIfAbsent(PanelDescriptors.LOAD, () -> new LoadPanelManager());
        addPanelManagerIfAbsent(PanelDescriptors.MENU, () -> new MenuPanelManager());
        addPanelManagerIfAbsent(PanelDescriptors.MESSAGE, () -> new MessagePanelManager());
        addPanelManagerIfAbsent(PanelDescriptors.CONNECT, () -> new JoinMPGamePanelManager());
        addPanelManagerIfAbsent(PanelDescriptors.START_SERVER, () -> new StartServerPanelManager());
    }
    
    protected final void addPanelManagerIfAbsent (PanelDescriptor pd, Supplier<PanelManagerBase> creator)
    {
        if (!panelMgrs.containsKey(pd))
        {
            panelMgrs.put(pd, creator.get());
        }
    }
    
    private void foreach(Map<? extends PanelDescriptor, ? extends IPanelManager> panelMgrs, Consumer<IPanelManager> func)
    {
        for (IPanelManager panelMgr : panelMgrs.values())
        {
            if (panelMgr.isCreated())
            {
                func.accept(panelMgr);
            }
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
        IDataPanelManager<IN, OUT> panelMgr = (IDataPanelManager<IN, OUT>) panelMgrs.get(panelDesc);
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
        return new ArrayList<>(panelMgrs.keySet());
    }
}
