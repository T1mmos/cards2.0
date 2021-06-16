package gent.timdemey.cards.ui.panels;

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
import gent.timdemey.cards.ui.panels.about.AboutPanelManager;
import gent.timdemey.cards.ui.panels.dialogs.message.MessagePanelManager;
import gent.timdemey.cards.ui.panels.dialogs.mp.JoinMPGamePanelManager;
import gent.timdemey.cards.ui.panels.dialogs.mp.LobbyPanelManager;
import gent.timdemey.cards.ui.panels.dialogs.mp.StartServerPanelManager;
import gent.timdemey.cards.ui.panels.game.GameMenuPanelManager;
import gent.timdemey.cards.ui.panels.game.GamePanelManager;
import gent.timdemey.cards.ui.panels.load.LoadPanelManager;
import gent.timdemey.cards.ui.panels.menu.MenuPanelManager;
import gent.timdemey.cards.ui.panels.settings.SettingsPanelManager;

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
        
        foreach(IPanelManager::preload, false);
    }
    
    protected void addAbsentPanelManagers()
    {
        addPanelManagerIfAbsent(PanelDescriptors.About, () -> new AboutPanelManager());
        addPanelManagerIfAbsent(PanelDescriptors.Connect, () -> new JoinMPGamePanelManager());
        addPanelManagerIfAbsent(PanelDescriptors.Game, () -> new GamePanelManager());
        addPanelManagerIfAbsent(PanelDescriptors.GameMenu, () -> new GameMenuPanelManager());
        addPanelManagerIfAbsent(PanelDescriptors.Load, () -> new LoadPanelManager());
        addPanelManagerIfAbsent(PanelDescriptors.Lobby, () -> new LobbyPanelManager());
        addPanelManagerIfAbsent(PanelDescriptors.Menu, () -> new MenuPanelManager());
        addPanelManagerIfAbsent(PanelDescriptors.Message, () -> new MessagePanelManager());
        addPanelManagerIfAbsent(PanelDescriptors.Settings, () -> new SettingsPanelManager());
        addPanelManagerIfAbsent(PanelDescriptors.StartServer, () -> new StartServerPanelManager());
    }
    
    protected final void addPanelManagerIfAbsent (PanelDescriptor pd, Supplier<PanelManagerBase> creator)
    {
        if (!panelMgrs.containsKey(pd))
        {
            panelMgrs.put(pd, creator.get());
        }
    }
    
    private void foreach(Consumer<IPanelManager> func, boolean onlyIfCreated)
    {
        for (IPanelManager panelMgr : panelMgrs.values())
        {
            if (!onlyIfCreated || panelMgr.isPanelCreated())
            {
                func.accept(panelMgr);
            }
        }
    }

    @Override
    public PanelDescriptor getDefaultPanelDescriptor()
    {
        return PanelDescriptors.Menu;
    }
    
    @Override
    public IPanelManager getPanelManager(PanelDescriptor panelDesc)
    {
        return getPanelManagerPriv(panelDesc);
    }    

    @Override
    public <IN, OUT> IDataPanelManager<IN, OUT> getPanelManager(DataPanelDescriptor<IN, OUT> panelDesc)
    {
        return getPanelManagerPriv(panelDesc);
    }
    
    private <T> T getPanelManagerPriv (PanelDescriptor panelDesc)
    {
        IPanelManager panelMgr = panelMgrs.get(panelDesc);
        if (panelMgr == null)
        {
            throw new IllegalArgumentException("PanelDescriptor isn't mapped onto a PanelManager: " + panelDesc);
        }
        
        @SuppressWarnings("unchecked")
        T he = (T) panelMgr;
        return he;
    }
    
    @Override
    public final void rescaleResourcesAsync(Runnable callback)
    {
        IScalingService scaleServ = Services.get(IScalingService.class);
        List<RescaleRequest> requests = new ArrayList<>();
        foreach(mgr -> mgr.createRescaleRequests(requests), true);
        scaleServ.rescaleAsync(requests, () -> 
        {
            SwingUtilities.invokeLater(callback);           
        });
    }

    @Override
    public void createScalableComponents()
    {
        foreach(IPanelManager::createSComponents, true);
    }
    
    @Override
    public void positionScalableComponents()
    {
        foreach(IPanelManager::positionSComponents, true); 
    }
    


    @Override
    public void repaintScalableComponents()
    {
        foreach(IPanelManager::repaintSComponents, true); 
    }

    @Override
    public List<PanelDescriptor> getPanelDescriptors()
    {
        return new ArrayList<>(panelMgrs.keySet());
    }
}
