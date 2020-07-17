package gent.timdemey.cards;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.logging.LogLevel;
import gent.timdemey.cards.logging.LogManager;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.serialization.SerializationService;
import gent.timdemey.cards.services.configman.ConfigManager;
import gent.timdemey.cards.services.context.CommandNetworkService;
import gent.timdemey.cards.services.context.ContextService;
import gent.timdemey.cards.services.gamepanel.AnimationService;
import gent.timdemey.cards.services.interfaces.IAnimationService;
import gent.timdemey.cards.services.interfaces.IConfigManager;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.INetworkService;
import gent.timdemey.cards.services.interfaces.IResourceRepository;
import gent.timdemey.cards.services.interfaces.IResourceService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.services.interfaces.ISerializationService;
import gent.timdemey.cards.services.resources.ResourceRepository;
import gent.timdemey.cards.services.resources.ResourceService;
import gent.timdemey.cards.services.scaling.ScalingService;
import gent.timdemey.cards.ui.StartFrame;

public class Start
{
    private Start()
    {
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> bootUI(args));
    }

    public static void bootUI(String[] args)
    {
        installAllServices(args);
        StartFrame.StartUI();
    }

    private static void installAllServices(String[] args)
    {
        Services services = new Services();
        App.init(services);
        Start.installBaseServices();

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
        
        Services.get(IResourceRepository.class).loadRepositories();
        
        Loc.setLocale(Loc.AVAILABLE_LOCALES[0]);
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

   //     IResourceRepository resRepo = new ResourceRepository();
      //  App.getServices().install(IResourceRepository.class, resRepo);
    }

    private static void installServices()
    {
        Services services = App.getServices();
        if(!Services.isInstalled(IConfigManager.class))
        {
            IConfigManager configManager = new ConfigManager();
            App.getServices().install(IConfigManager.class, configManager);
        }
        if(!Services.isInstalled(IContextService.class))
        {
            IContextService ctxtProv = new ContextService();
            services.install(IContextService.class, ctxtProv);
        }
        if (!Services.isInstalled(IScalingService.class))
        {
            IScalingService scaleServ = new ScalingService();
            services.install(IScalingService.class, scaleServ);
        }
        
        // resources
        if(!Services.isInstalled(IResourceService.class))
        {
            IResourceService resServ = new ResourceService();
            services.install(IResourceService.class, resServ);
        }
        if(!Services.isInstalled(IResourceRepository.class))
        {
            IResourceRepository resRepo = new ResourceRepository();
            services.install(IResourceRepository.class, resRepo);
        }
        
        // network related
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
        
        // animations
        if (!Services.isInstalled(IAnimationService.class))
        {
            IAnimationService animServ = new AnimationService();
            services.install(IAnimationService.class, animServ);
        }
    }
}
