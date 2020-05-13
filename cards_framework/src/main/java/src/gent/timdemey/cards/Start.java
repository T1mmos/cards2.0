package gent.timdemey.cards;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.logging.LogLevel;
import gent.timdemey.cards.logging.LogManager;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.serialization.SerializationService;
import gent.timdemey.cards.services.IConfigManager;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.IImageService;
import gent.timdemey.cards.services.INetworkService;
import gent.timdemey.cards.services.IResourceManager;
import gent.timdemey.cards.services.IScalableImageManager;
import gent.timdemey.cards.services.ISerializationService;
import gent.timdemey.cards.services.ISoundManager;
import gent.timdemey.cards.services.configman.ConfigManager;
import gent.timdemey.cards.services.context.CommandNetworkService;
import gent.timdemey.cards.services.context.ContextService;
import gent.timdemey.cards.services.images.ImageService;
import gent.timdemey.cards.services.resman.ResourceManager;
import gent.timdemey.cards.services.scaleman.ScalableImageManager;
import gent.timdemey.cards.services.soundman.SoundManager;
import gent.timdemey.cards.ui.StartFrame;

public class Start
{
    private Start()
    {
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> boot(args));
    }

    public static void boot(String[] args)
    {
        installAllServices(args);
        StartFrame.StartUI();
    }

    private static void installAllServices(String[] args)
    {
        Services services = new Services();
        App.init(services);
        Start.installBaseServices();
        Loc.setLocale(Loc.AVAILABLE_LOCALES[0]);

        // determine plugin
        ICardPlugin plugin = getCardPlugin(args);
        if(plugin == null)
        {
            System.err.println("Cannot load plugin class. Terminating.");
            return;
        }

        services.install(ICardPlugin.class, plugin);

        plugin.installServices();
        Start.installServices();
    }

    private static ICardPlugin getCardPlugin(String[] args)
    {
        if(args.length != 1)
        {
            System.err.println("A single argument is expected, but " + args.length + " were given.");
            return null;
        }

        String clazzName = args[0];
        Class<?> clazz;
        try
        {
            clazz = Class.forName(clazzName);
        }
        catch (ClassNotFoundException e)
        {
            Logger.error("The given class %s is not found in the classpath.", clazzName);
            return null;
        }

        if(!ICardPlugin.class.isAssignableFrom(clazz))
        {
            System.err.println("Should provide a card plugin. The given class does not derive from " + ICardPlugin.class.getSimpleName() + ".");
            return null;
        }

        Class<? extends ICardPlugin> pluginClazz = (Class<? extends ICardPlugin>) clazz;
        ICardPlugin plugin = null;
        try
        {
            plugin = pluginClazz.getDeclaredConstructor().newInstance();
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | SecurityException | NoSuchMethodException e)
        {
            Logger.error("The given plugin class cannot be instantiated.", e);
            return null;
        }

        return plugin;
    }

    private static void installBaseServices()
    {
        ILogManager logMan = new LogManager();
        logMan.setLogLevel(LogLevel.TRACE);
        App.getServices().install(ILogManager.class, logMan);

        IResourceManager resMan = new ResourceManager();
        App.getServices().install(IResourceManager.class, resMan);
    }

    private static void installServices()
    {
        Services services = App.getServices();
        if(!Services.isInstalled(IConfigManager.class))
        {
            IConfigManager configManager = new ConfigManager();
            App.getServices().install(IConfigManager.class, configManager);
        }
        if(!Services.isInstalled(IScalableImageManager.class))
        {
            IScalableImageManager scaleImgMan = new ScalableImageManager();
            services.install(IScalableImageManager.class, scaleImgMan);
        }
        if(!Services.isInstalled(IImageService.class))
        {
            IImageService imageService = new ImageService();
            services.install(IImageService.class, imageService);
        }
        if(!Services.isInstalled(ISoundManager.class))
        {
            ISoundManager sndMan = new SoundManager();
            services.install(ISoundManager.class, sndMan);
        }
        if(!Services.isInstalled(IContextService.class))
        {
            IContextService ctxtProv = new ContextService();
            services.install(IContextService.class, ctxtProv);
        }
        if(!Services.isInstalled(ISerializationService.class))
        {
            ISerializationService serServ = new SerializationService();
            services.install(ISerializationService.class, serServ);
        }
        if (!Services.isInstalled(INetworkService.class))
        {
            INetworkService nServ = new CommandNetworkService();
            services.install(INetworkService.class, nServ);
        }
    }
}
