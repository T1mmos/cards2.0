package gent.timdemey.cards.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
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
import java.awt.Window;
import net.miginfocom.swing.MigLayout;

public class StartUI
{
    private static JFrame frame;
    
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
            
            Logger.info("loading " + "/img/splash.png");
            try (InputStream is = StartUI.class.getResourceAsStream("/img/splash.png"))
            {
                bi = ImageIO.read(is);
            }
            catch (Exception e)
            {
                Logger.error(e);
                return;  // don't fail or throw, simply no splash                    
            }
            
            ImageIcon ii = new ImageIcon(bi);
            JLabel icon = new JLabel(ii);
            panel.add(icon);
        }
        
        panel.setBorder(null);
        
        frame = new JFrame("splash");
        frame.setType(Window.Type.UTILITY);
        frame.setUndecorated(true);
        frame.setContentPane(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private static void preload()
    {
        FlatDarkLaf.setup();
        Services.preload();
    }
    
    private static void onServicesPreloaded(Async async)
    {
        if (!async.success)
        {
            return;
        }
        
        frame.setVisible(false);
        
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
