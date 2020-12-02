package gent.timdemey.cards.ui;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.App;
import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.model.entities.commands.C_ImportExportStateUI;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptor;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.dialogs.DialogService;
import gent.timdemey.cards.services.frame.FrameService;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IDialogService;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.panels.GamePanelStateListener;
import gent.timdemey.cards.services.panels.PanelService;
import gent.timdemey.cards.ui.actions.ActionService;
import gent.timdemey.cards.ui.actions.IActionService;

public class StartFrame
{
    private StartFrame()
    {
    }

    public static void installUiServices(Services services)
    {
        services.installIfAbsent(IFrameService.class, () -> new FrameService());
        services.installIfAbsent(IDialogService.class, () -> new DialogService());
        services.installIfAbsent(IActionService.class, () -> new ActionService());
        services.installIfAbsent(IPanelService.class, () -> new PanelService());
        services.installIfAbsent(IStateListener.class, () -> new GamePanelStateListener());
    }

    public static void StartUI()
    {    
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());
        ICardPlugin plugin = Services.get(ICardPlugin.class);        
        
        IContextService ctxtServ = Services.get(IContextService.class);
        ctxtServ.initialize(ContextType.UI);
        Context ctxt = ctxtServ.getThreadContext();
        
        Services services = App.getServices();
        plugin.installUiServices(services);
        StartFrame.installUiServices(services);
        
        IFrameService frameServ = Services.get(IFrameService.class);
        IPanelService panelServ = Services.get(IPanelService.class);
        
        Services.preload();
        Loc.setLocale(Loc.AVAILABLE_LOCALES[0]);
        
        C_ImportExportStateUI cmd_readConfig = new C_ImportExportStateUI(true);
        ctxt.schedule(cmd_readConfig);        

        JFrame frame = frameServ.getFrame();
        // add different top-level panels (e.g. menu, game, overlay, ...)
        List<PanelDescriptor> panelDescs = panelServ.getPanelDescriptors();
        for (PanelDescriptor pDesc : panelDescs)
        {
            JComponent comp = panelServ.getPanel(pDesc);
            frameServ.addPanel(pDesc, comp);
        }

        ctxt.addStateListener(new GameBootListener());
        ctxt.addStateListener(new StateExportListener());
        
        frameServ.showPanel(PanelDescriptors.MENU);
        frame.setVisible(true);    
    }
}
