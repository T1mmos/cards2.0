package gent.timdemey.cards.ui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.C_LoadConfig;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptor;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.utils.Async;
import gent.timdemey.cards.utils.ThreadUtils;
import net.miginfocom.swing.MigLayout;

public class StartUI
{
    private static JDialog dialog;
    
    private StartUI()
    {
    }
    
    public static void startUI()
    {    
        ThreadUtils.checkEDT();

        showSplash();
        
        // let all services preload load in the background
        ThreadUtils.executeAndContinueOnUi("Service Preloader", StartUI::preload, StartUI::onServicesPreloaded);
    }
    
    private static void showSplash()
    {
        JPanel panel = new JPanel(new MigLayout("insets 0"));
        
        // background image
        {
            BufferedImage bi;            
            try (InputStream is = StartUI.class.getResourceAsStream("splash.png"))
            {
                bi = ImageIO.read(is);
            }
            catch (IOException e)
            {
                Logger.error(e);                    
                return;  // don't fail or throw, simply no splash
            }
            
            ImageIcon ii = new ImageIcon(bi);
            JLabel icon = new JLabel(ii);
            panel.add(icon);
        }
        
        panel.setBorder(null);
        
        dialog = new JDialog();
        dialog.setUndecorated(true);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
    
    private static void preload()
    {
        Services.preload();
    }
    
    private static void onServicesPreloaded(Async async)
    {
        if (!async.success)
        {
            return;
        }
        
        dialog.setVisible(false);
        
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
        C_LoadConfig cmd_loadcfg = new C_LoadConfig();
        ctxt.schedule(cmd_loadcfg);
        
        frameServ.installStateListeners();
        
        // the frame is visible and created so the frame services can 
        // give the available dimensions to the position service
        frameServ.updatePositionService();
        
        // add and show the default panel
        IPanelService panelServ = Services.get(IPanelService.class);
        PanelDescriptor panelDesc = panelServ.getDefaultPanelDescriptor();
        frameServ.showPanel(panelDesc);
    }
}
