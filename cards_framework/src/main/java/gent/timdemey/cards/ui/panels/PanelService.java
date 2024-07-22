package gent.timdemey.cards.ui.panels;

import gent.timdemey.cards.di.Container;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

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
import gent.timdemey.cards.ui.panels.frame.FramePanelManager;
import gent.timdemey.cards.ui.panels.game.CardGamePanelManager;
import gent.timdemey.cards.ui.panels.game.GameMenuPanelManager;
import gent.timdemey.cards.ui.panels.load.LoadPanelManager;
import gent.timdemey.cards.ui.panels.menu.MenuPanelManager;
import gent.timdemey.cards.ui.panels.settings.SettingsPanelManager;

public class PanelService implements IPanelService
{
    private Map<PanelDescriptor, IPanelManager> panelMgrs;
    
    private final Container _Container;
    private final IScalingService _ScalingService;
    private final IPanelService _PanelService;

    public PanelService(
            Container container,
            IScalingService scalingService,
            IPanelService panelService)
    {
        this._Container = container;
        this._ScalingService = scalingService;
        this._PanelService = panelService;
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
        addPanelManagerIfAbsent(PanelDescriptors.Frame, () -> _Container.Get(FramePanelManager.class));
        addPanelManagerIfAbsent(PanelDescriptors.About, () -> _Container.Get(AboutPanelManager.class));
        addPanelManagerIfAbsent(PanelDescriptors.Connect, () -> _Container.Get(JoinMPGamePanelManager.class));
        addPanelManagerIfAbsent(PanelDescriptors.Game, () -> _Container.Get(CardGamePanelManager.class));
        addPanelManagerIfAbsent(PanelDescriptors.GameMenu, () -> _Container.Get(GameMenuPanelManager.class));
        addPanelManagerIfAbsent(PanelDescriptors.Load, () -> _Container.Get(LoadPanelManager.class));
        addPanelManagerIfAbsent(PanelDescriptors.Lobby, () -> _Container.Get(LobbyPanelManager.class));
        addPanelManagerIfAbsent(PanelDescriptors.Menu, () -> _Container.Get(MenuPanelManager.class));
        addPanelManagerIfAbsent(PanelDescriptors.Message, () -> _Container.Get(MessagePanelManager.class));
        addPanelManagerIfAbsent(PanelDescriptors.Settings, () -> _Container.Get(SettingsPanelManager.class));
        addPanelManagerIfAbsent(PanelDescriptors.StartServer, () -> _Container.Get(StartServerPanelManager.class));
    }

    protected final void addPanelManagerIfAbsent(PanelDescriptor pd, Supplier<PanelManagerBase> creator)
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

    private <T> T getPanelManagerPriv(PanelDescriptor panelDesc)
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
        List<RescaleRequest> requests = new ArrayList<>();
        foreach(mgr -> mgr.addRescaleRequests(requests), true);

        _ScalingService.rescaleAsync(requests, () ->
        {
            SwingUtilities.invokeLater(() -> onRescaledResources(callback));
        });
    }

    private void onRescaledResources(Runnable externalCallback)
    {
        if (externalCallback != null)
        {
            externalCallback.run();
        }

        for (PanelDescriptor pd : _PanelService.getPanelDescriptors())
        {
            IPanelManager pm = _PanelService.getPanelManager(pd);
            JComponent panel = pm.getPanel();
            if (panel == null)
            {
                continue;
            }

            panel.repaint();
        }
    }

    @Override
    public void createComponentsAsync(Runnable callback)
    {
        List<Runnable> runnables = new ArrayList<>();
        foreach(mgr -> mgr.addComponentCreators(runnables), true);
        
        // put all runnables in a stack which the executor service will take work from
        final Stack<Runnable> stack = new Stack<>();
        stack.addAll(runnables);

        // we will ensure that not too much stuff is working on the UI thread, therefore
        // work is scheduled on the UI thread with fixed delays between work units, leaving 
        // space for e.g. animations 
        final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor(new ThreadFactory()
        {            
            @Override
            public Thread newThread(Runnable r)
            {
                Thread thr = new Thread(r, "CreateComponentsAsync");
                thr.setDaemon(true);
                return thr;
            }
        });
        exec.scheduleWithFixedDelay(() ->
        {
            if (stack.isEmpty())
            {
                exec.shutdown();
                SwingUtilities.invokeLater(callback);
                return;
            }
            
            Runnable compCreator = stack.pop();
            SwingUtilities.invokeLater(compCreator);
        }, 100, 500, TimeUnit.MICROSECONDS);
    }

    @Override
    public void positionComponents()
    {
        foreach(IPanelManager::positionComponents, true);
    }

    @Override
    public List<PanelDescriptor> getPanelDescriptors()
    {
        return new ArrayList<>(panelMgrs.keySet());
    }
}
