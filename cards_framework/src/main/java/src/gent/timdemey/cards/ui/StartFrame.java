package gent.timdemey.cards.ui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.alee.laf.WebLookAndFeel;
import com.google.common.base.Preconditions;

import gent.timdemey.cards.App;
import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
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
import gent.timdemey.cards.ui.actions.ActionFactory;
import gent.timdemey.cards.ui.actions.ActionService;
import gent.timdemey.cards.ui.actions.IActionFactory;
import gent.timdemey.cards.ui.actions.IActionService;

public class StartFrame
{
    private StartFrame()
    {
    }

    public static void installUiServices()
    {
        Services services = App.getServices();
        if (!Services.isInstalled(IFrameService.class))
        {
            IFrameService frameServ = new FrameService();
            services.install(IFrameService.class, frameServ);
        }
        if (!Services.isInstalled(IDialogService.class))
        {
            IDialogService dialogService = new DialogService();
            services.install(IDialogService.class, dialogService);
        }
        if (!Services.isInstalled(IActionService.class))
        {
            IActionService actionService = new ActionService();
            services.install(IActionService.class, actionService);
        }
        
        if (!Services.isInstalled(IActionFactory.class))
        {
            IActionFactory actionFactory = new ActionFactory();
            services.install(IActionFactory.class, actionFactory);
        }
        if (!Services.isInstalled(IPanelService.class))
        {
            IPanelService gamePanelMan = new PanelService();
            services.install(IPanelService.class, gamePanelMan);
        }
        if (!Services.isInstalled(IStateListener.class))
        {
            IStateListener stateListener = new GamePanelStateListener();
            services.install(IStateListener.class, stateListener);
        }
    }

    public static void StartUI()
    {    
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());
        ICardPlugin plugin = Services.get(ICardPlugin.class);        
        
        IContextService ctxtServ = Services.get(IContextService.class);
        ctxtServ.initialize(ContextType.UI);
        Context ctxt = ctxtServ.getThreadContext();
        
        plugin.installUiServices();
        StartFrame.installUiServices();
        
        IFrameService frameServ = Services.get(IFrameService.class);
        IPanelService panelServ = Services.get(IPanelService.class);
        
        Services.preload();
        
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
        
        frameServ.setPanel(PanelDescriptors.MENU);
        frame.setVisible(true);    
    }
}
