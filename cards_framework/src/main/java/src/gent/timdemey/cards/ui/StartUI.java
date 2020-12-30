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

public class StartUI
{
    private StartUI()
    {
    }
    
    public static void startUI()
    {    
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());
        
        // let all services preload
        Services.preload();
        
        // locale
        Loc.setLocale(Loc.AVAILABLE_LOCALES[0]);
        
        // initialize UI context
        IContextService ctxtServ = Services.get(IContextService.class);
        ctxtServ.initialize(ContextType.UI);
        
        // show the frame with just the loading animation, but already with a certain size
        IFrameService frameServ = Services.get(IFrameService.class);                
        frameServ.getFrame().setVisible(true);
        
        // import configuration
        Context ctxt = ctxtServ.getThreadContext();
        C_ImportExportStateUI cmd_readConfig = new C_ImportExportStateUI(true);
        ctxt.schedule(cmd_readConfig);     
        ctxt.addStateListener(new GameBootListener());
        ctxt.addStateListener(new StateExportListener());
        
        // the frame is visible and created so the frame services can 
        // give the available dimensions to the position service
        frameServ.updatePositionService();
        
        // add and show the default panel
        IPanelService panelServ = Services.get(IPanelService.class);
        PanelDescriptor panelDesc = panelServ.getDefaultPanelDescriptor();
        //frameServ.addPanel(panelDesc);
        frameServ.showPanel(panelDesc);
    }
}
