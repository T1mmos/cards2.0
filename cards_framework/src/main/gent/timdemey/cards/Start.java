package gent.timdemey.cards;

import javax.swing.SwingUtilities;

import gent.timdemey.cards.entities.ContextProvider;
import gent.timdemey.cards.entities.IContextProvider;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.logging.LogManager;
import gent.timdemey.cards.services.configman.ConfigManager;
import gent.timdemey.cards.services.configman.IConfigManager;
import gent.timdemey.cards.services.gamepanel.GamePanelManager;
import gent.timdemey.cards.services.gamepanel.IGamePanelManager;
import gent.timdemey.cards.services.images.IImageService;
import gent.timdemey.cards.services.images.ImageService;
import gent.timdemey.cards.services.resman.IResourceManager;
import gent.timdemey.cards.services.resman.ResourceManager;
import gent.timdemey.cards.services.scaleman.IScalableImageManager;
import gent.timdemey.cards.services.scaleman.ScalableImageManager;
import gent.timdemey.cards.services.soundman.ISoundManager;
import gent.timdemey.cards.services.soundman.SoundManager;
import gent.timdemey.cards.ui.StartFrame;

public class Start {
    private static ICardPlugin getCardPlugin(String [] args) 
    {
        if (args.length != 1)
        {
            System.err.println("A single argument is expected, but " + args.length + " were given.");
            return null;
        }
        
        String clazzName = args[0];
        Class<?> clazz;
        try {
            clazz = Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            System.err.println("The given class " + clazzName + " is not found in the classpath.");
            return null;
        }
        
        if (!ICardPlugin.class.isAssignableFrom(clazz))
        {
            System.err.println("Should provide a card plugin. The given class does not derive from " + ICardPlugin.class.getSimpleName() + ".");
            return null;
        }
        
        Class<? extends ICardPlugin> pluginClazz = (Class<? extends ICardPlugin>) clazz;
        ICardPlugin plugin = null;
        try {
            plugin = pluginClazz.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e) {
            System.err.println("The given plugin class cannot be instantiated.");
            return null;
        }

        return plugin;
    }
    
    private static void installBaseServices()
    {
        ILogManager logMan = new LogManager();
        Services.install(ILogManager.class, logMan);
   
        IResourceManager resMan = new ResourceManager();
        Services.install(IResourceManager.class, resMan);    
    }
    
    private static void installServices()
    {        
        if (!Services.isInstalled(IConfigManager.class))
        {
            IConfigManager configManager = new ConfigManager();
            Services.install(IConfigManager.class, configManager);
        }
        if (!Services.isInstalled(IScalableImageManager.class))
        {
            IScalableImageManager scaleImgMan = new ScalableImageManager();
            Services.install(IScalableImageManager.class, scaleImgMan);
        }
        if (!Services.isInstalled(IGamePanelManager.class))
        {
            IGamePanelManager gamePanelMan = new GamePanelManager();
            Services.install(IGamePanelManager.class, gamePanelMan);
        }
        if (!Services.isInstalled(IImageService.class))
        {
            IImageService imageService = new ImageService();
            Services.install(IImageService.class, imageService);
        }
        if (!Services.isInstalled(ISoundManager.class))
        {
            ISoundManager sndMan = new SoundManager();
            Services.install(ISoundManager.class, sndMan);
        }
        if (!Services.isInstalled(IContextProvider.class))
        {
            IContextProvider ctxtProv = new ContextProvider();
            Services.install(IContextProvider.class, ctxtProv);
        }
    }
    
    public static void main(String[] args) 
    {        
        SwingUtilities.invokeLater(() -> 
        {
            Start.installBaseServices();
            Loc.setLocale(Loc.AVAILABLE_LOCALES[0]);
            
            // determine plugin
            ICardPlugin plugin = getCardPlugin(args);
            if (plugin == null)
            {
                System.err.println("Cannot load plugin class. Terminating.");
                return;
            }
            
            Services.install(ICardPlugin.class, plugin);
            plugin.installServices();
                        
            Start.installServices();
            StartFrame.installUiServices();

            StartFrame.StartUI(plugin);
        });        
    }
}
