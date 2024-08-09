package gent.timdemey.cards.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.cfg.C_Load;
import gent.timdemey.cards.model.entities.commands.CommandFactory;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptor;
import gent.timdemey.cards.services.contract.preload.IPreload;
import gent.timdemey.cards.services.contract.preload.PreloadOrder;
import gent.timdemey.cards.services.contract.preload.PreloadOrderType;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.utils.Async;
import gent.timdemey.cards.utils.ThreadUtils;
import java.awt.Window;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.SwingUtilities;
import net.miginfocom.swing.MigLayout;
import gent.timdemey.cards.services.context.ICommandExecutor;

public class StartUI
{
    private static JFrame frame;
    
    private final IFrameService _FrameService;
    
    private final Container _Container;
    private final ICommandExecutor _CommandExecutor;
    private final IPanelService _PanelService;
    private final Logger _Logger;
    private final Loc _Loc;
    private final CommandFactory _CommandFactory;
    private final Context _Context;
    
    public StartUI(
        Container container, 
        Context context,
        ICommandExecutor commandExecutor,
        IFrameService frameService, 
        IPanelService panelService,
        CommandFactory commandFactory,
        Logger logger,
        Loc loc)
    {
        this._Container = container;
        this._Context = context;
        this._CommandExecutor = commandExecutor;
        this._FrameService = frameService;
        this._PanelService = panelService;
        this._CommandFactory = commandFactory;
        this._Logger = logger;
        this._Loc = loc;
    }
    
    public void startUI()
    {    
        SwingUtilities.invokeLater(this::startOnEDT);
    }
    
    private void startOnEDT()
    {
        _Context.initialize();
        
        showSplash();
        
        // let all services preload load in the background
        ThreadUtils.executeAndContinueOnUi(_Logger, "Service Preloader", this::preload, this::onServicesPreloaded);
    }
    
    private void showSplash()
    {
        JPanel panel = new JPanel(new MigLayout("insets 0"));
        
        // background image
        {
            BufferedImage bi; 
            
            _Logger.info("loading " + "/img/splash.png");
            try (InputStream is = StartUI.class.getResourceAsStream("/img/splash.png"))
            {
                bi = ImageIO.read(is);
            }
            catch (Exception e)
            {
                _Logger.error(e);
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
        
    private void preload()
    {
        FlatDarkLaf.setup();
        
        // take a copy as parent interface request add more EntryKey to the service map. 
        // this solves ConcurrentModificationExceptions and equally important we only want to preload once.
        
        
        List<IPreload> preloadables = _Container.GetAllInstances().stream()
                .filter(obj -> obj instanceof IPreload)
                .map(obj -> (IPreload) obj)
                .collect(Collectors.toList());
        
        List<IPreload> pl_isolated =  preloadables.stream().filter(p -> readPreloadAnnotation(p) == PreloadOrderType.ISOLATED).collect(Collectors.toList());
        List<IPreload> pl_dependent =  preloadables.stream().filter(p -> readPreloadAnnotation(p) == PreloadOrderType.DEPENDENT).collect(Collectors.toList());
       
        if (preloadables.size() != pl_isolated.size() + pl_dependent.size())
        {
            throw new UnsupportedOperationException("The list of preloadables contains entries with unknown PreloadOrderType annotations");
        }
            
        // load first all isolated preloadables
        for (IPreload preloadable : pl_isolated)
        {
            preloadable.preload();
        }
        
        // then load all preloadables that depend on others (currently only one dependency layer supported)
        for (IPreload preloadable : pl_dependent)
        {
            preloadable.preload();
        }
    }
    
    private static PreloadOrderType readPreloadAnnotation(IPreload preloadable)
    {
        try
        {
            Method m = IPreload.class.getDeclaredMethods()[0];
            Class<?> clz = preloadable.getClass();
            while (true)
            {
                Method m_impl = clz.getMethod(m.getName());
                
                PreloadOrder[] annots = m_impl.getAnnotationsByType(PreloadOrder.class);
                if (annots.length == 1)
                {
                    return annots[0].order();
                }
                

                clz = clz.getSuperclass();
            }
        }
        catch (Exception e)
        {
            return PreloadOrderType.DEPENDENT;
        }        
    }
    
    private void onServicesPreloaded(Async async)
    {
        if (!async.success)
        {
            return;
        }
        
        frame.setVisible(false);
                
        // import configuration
        C_Load cmd_loadcfg = _CommandFactory.CreateLoadConfig();
        _CommandExecutor.run(cmd_loadcfg);
        
        // locale
        _Loc.setLocale(Loc.AVAILABLE_LOCALES[0]);
                
        // show the frame with just the loading animation, but already with a certain size
        _FrameService.getFrame().setVisible(true);
        
      
        
        _FrameService.installStateListeners();
        
        // the frame is visible and created so the frame services can 
        // give the available dimensions to the position service
        _FrameService.updatePositionService();
        
        // add and show the default panel
        PanelDescriptor panelDesc = _PanelService.getDefaultPanelDescriptor();
        _FrameService.showPanel(panelDesc);
    }
}
