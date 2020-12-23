package gent.timdemey.cards.ui;

import javax.swing.SwingUtilities;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.model.entities.commands.C_ImportExportStateUI;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptor;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.IPositionService;

public class StartUI
{
    private StartUI()
    {
    }

    public static void startUI()
    {    
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());
                       
        // show the frame with just the loading animation, but already with a certain size
        IFrameService frameServ = Services.get(IFrameService.class);                
        frameServ.getFrame().setVisible(true);

        // locale
        Loc.setLocale(Loc.AVAILABLE_LOCALES[0]);
        
        // let all services preload
        Services.preload();
        
        // read configuration
        IContextService ctxtServ = Services.get(IContextService.class);
        ctxtServ.initialize(ContextType.UI);
        Context ctxt = ctxtServ.getThreadContext();
        C_ImportExportStateUI cmd_readConfig = new C_ImportExportStateUI(true);
        ctxt.schedule(cmd_readConfig);     
        ctxt.addStateListener(new GameBootListener());
        ctxt.addStateListener(new StateExportListener());
        
        // the frame is visible and created so the frame services can 
        // give the available dimensions to the position service
        frameServ.updatePositionService();
        
        // and now we can start rescaling the resources according to the
        // dimensions
        IPanelService panelServ = Services.get(IPanelService.class);
        panelServ.rescaleResourcesAsync(StartUI::onRescaledResources);
    }
    
    private static void onRescaledResources ()
    {
        IPanelService panelServ = Services.get(IPanelService.class);
        IFrameService frameServ = Services.get(IFrameService.class);
        
        // add different top-level panels (e.g. menu, game, overlay, ...)        
        for (PanelDescriptor pd : panelServ.getPanelDescriptors())
        {
            frameServ.addPanel(pd);
        }
        
        // bring the default panel to the front
        PanelDescriptor panelDesc = panelServ.getDefaultPanelDescriptor();
        frameServ.showPanel(panelDesc);
        
        // the resources have loaded and are rescaled, so create and position 
        // the components that use them
        panelServ.createScalableComponents();
        panelServ.positionScalableComponents();
        
        // finally bring the main panel alive
        frameServ.getFrame().setVisible(true);
    }

}
