package gent.timdemey.cards;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.logging.LogLevel;
import gent.timdemey.cards.logging.LogManager;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.serialization.SerializationService;
import gent.timdemey.cards.services.action.ActionService;
import gent.timdemey.cards.services.config.ConfigService;
import gent.timdemey.cards.services.context.CommandNetworkService;
import gent.timdemey.cards.services.context.ContextService;
import gent.timdemey.cards.services.frame.FrameService;
import gent.timdemey.cards.services.interfaces.IActionService;
import gent.timdemey.cards.services.interfaces.IAnimationService;
import gent.timdemey.cards.services.interfaces.IConfigService;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.INetworkService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.IResourceLocationService;
import gent.timdemey.cards.services.interfaces.IResourceRepository;
import gent.timdemey.cards.services.interfaces.IResourceService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.services.interfaces.ISerializationService;
import gent.timdemey.cards.services.panels.AnimationService;
import gent.timdemey.cards.services.panels.PanelService;
import gent.timdemey.cards.services.panels.game.GamePanelStateListener;
import gent.timdemey.cards.services.resources.ResourceLocationService;
import gent.timdemey.cards.services.resources.ResourceRepository;
import gent.timdemey.cards.services.resources.ResourceService;
import gent.timdemey.cards.services.scaling.ScalingService;
import gent.timdemey.cards.ui.StartUI;

public class Start
{
    private Start()
    {
    }

    public static void main(String[] args)
    {
        // determine plugin
        ICardPlugin plugin = loadCardPlugin(args);
        if(plugin == null)
        {
            System.err.println("Cannot load plugin class. Terminating.");
            return;
        }

        installAllServices(plugin);
        SwingUtilities.invokeLater(StartUI::startUI);
    }
    
    private static void installAllServices(ICardPlugin plugin)
    {
        // create Services and set singleton
        Services services = new Services();        
        App.init(services);
        
        // install services that cannot be overruled and that are needed by
        // other services
        Start.installRootServices();
                
        // set the plugin services and let the plugin install its own services
        services.install(ICardPlugin.class, plugin);
        plugin.installServices(services);
        
        // now install services not set by the plugin
        Start.installServices();        
    }

    private static ICardPlugin loadCardPlugin(String[] args)
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

        @SuppressWarnings("unchecked")
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

    private static void installRootServices()
    {
        ILogManager logMan = new LogManager();
        logMan.setLogLevel(LogLevel.TRACE);
        App.getServices().install(ILogManager.class, logMan);
    }

    private static void installServices()
    {
        Services services = App.getServices();
      
        services.installIfAbsent(IConfigService.class, () -> new ConfigService());
        services.installIfAbsent(IContextService.class, () -> new ContextService());
        services.installIfAbsent(IScalingService.class, () -> new ScalingService());
        services.installIfAbsent(IResourceService.class, () -> new ResourceService());
        services.installIfAbsent(IResourceRepository.class, () -> new ResourceRepository());
        services.installIfAbsent(IResourceLocationService.class, () -> new ResourceLocationService());
        services.installIfAbsent(ISerializationService.class, () -> new SerializationService());
        services.installIfAbsent(INetworkService.class, () -> new CommandNetworkService());        
        services.installIfAbsent(IAnimationService.class, () -> new AnimationService()); 
        services.installIfAbsent(IFrameService.class, () -> new FrameService());
     //   services.installIfAbsent(IDialogService.class, () -> new DialogService());
        services.installIfAbsent(IActionService.class, () -> new ActionService());
        services.installIfAbsent(IPanelService.class, () -> new PanelService());
        services.installIfAbsent(IStateListener.class, () -> new GamePanelStateListener());
    }
}
