package gent.timdemey.cards;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.logging.LogLevel;
import gent.timdemey.cards.logging.LogManager;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.serialization.SerializationService;
import gent.timdemey.cards.services.action.ActionService;
import gent.timdemey.cards.services.animation.AnimationService;
import gent.timdemey.cards.services.config.ConfigService;
import gent.timdemey.cards.services.context.CommandNetworkService;
import gent.timdemey.cards.services.context.ContextService;
import gent.timdemey.cards.services.file.FileService;
import gent.timdemey.cards.services.frame.FrameService;
import gent.timdemey.cards.services.interfaces.IActionService;
import gent.timdemey.cards.services.interfaces.IAnimationService;
import gent.timdemey.cards.services.interfaces.IConfigurationService;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFileService;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.INetworkService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.IResourceCacheService;
import gent.timdemey.cards.services.interfaces.IResourceLocationService;
import gent.timdemey.cards.services.interfaces.IResourceRepository;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.services.interfaces.ISerializationService;
import gent.timdemey.cards.services.resources.ResourceCacheService;
import gent.timdemey.cards.services.resources.ResourceLocationService;
import gent.timdemey.cards.services.resources.ResourceRepository;
import gent.timdemey.cards.ui.StartUI;
import gent.timdemey.cards.ui.components.ScalingService;
import gent.timdemey.cards.ui.panels.PanelService;

public class Start
{
    private Start()
    {
    }

    public static void main(String[] args)
    {
        // install the service singleton
        installSingleton();
        
        // install services that cannot be overruled and that are immediately needed by code
        installRootServices();
        
        // determine plugin and if found, let it install services
        ICardPlugin plugin = loadCardPlugin(args);
        if(plugin == null)
        {
            Logger.error("Cannot load plugin class. Terminating.");
            return;
        }
        instalPluginServices(plugin);
        
        // now install remaining services that were not overruled by the plugin
        installBaseServices();  

        SwingUtilities.invokeLater(StartUI::startUI);
    }
    
    private static void installSingleton()
    {
        // create Services and set singleton
        Services services = new Services();        
        App.init(services);
    }
    
    private static void installRootServices()
    {
        Services services = App.getServices();
        
        services.installIfAbsent(ILogManager.class, () -> new LogManager(LogLevel.TRACE));
    }
    
    private static ICardPlugin loadCardPlugin(String[] args)
    {
        if(args.length != 1)
        {
            Logger.error("A single argument is expected, but %s were given.", args.length);
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
           
            Logger.error("The given class '%s' is not found in the classpath.", clazzName);
            return null;
        }

        if(!ICardPlugin.class.isAssignableFrom(clazz))
        {
            Logger.error("Should provide a card plugin. The given class does not derive from ''.", ICardPlugin.class.getSimpleName());
            return null;
        }

        @SuppressWarnings("unchecked")
        Class<? extends ICardPlugin> pluginClazz = (Class<? extends ICardPlugin>) clazz;
        ICardPlugin plugin = null;
        try
        {
            plugin = pluginClazz.getDeclaredConstructor().newInstance();
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | SecurityException | NoSuchMethodException e)
        {
            Logger.error("The given plugin class cannot be instantiated", e);
            return null;
        }

        return plugin;
    }

    private static void instalPluginServices(ICardPlugin plugin)
    {
        Services services = App.getServices();
                
        // set the plugin services and let the plugin install its own services
        services.install(ICardPlugin.class, plugin);
        plugin.installServices(services);      
    }

    private static void installBaseServices()
    {
        Services services = App.getServices();
      
        services.installIfAbsent(IConfigurationService.class, () -> new ConfigService());
        services.installIfAbsent(IContextService.class, () -> new ContextService());
        services.installIfAbsent(IScalingService.class, () -> new ScalingService());
        services.installIfAbsent(IResourceCacheService.class, () -> new ResourceCacheService());
        services.installIfAbsent(IResourceRepository.class, () -> new ResourceRepository());
        services.installIfAbsent(IResourceLocationService.class, () -> new ResourceLocationService());
        services.installIfAbsent(ISerializationService.class, () -> new SerializationService());
        services.installIfAbsent(INetworkService.class, () -> new CommandNetworkService());        
        services.installIfAbsent(IAnimationService.class, () -> new AnimationService()); 
        services.installIfAbsent(IFrameService.class, () -> new FrameService());
        services.installIfAbsent(IActionService.class, () -> new ActionService());
        services.installIfAbsent(IPanelService.class, () -> new PanelService());
        services.installIfAbsent(IFileService.class, () -> new FileService());
    }
}
