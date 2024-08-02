package gent.timdemey.cards;

import gent.timdemey.cards.di.Container;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.logging.LogLevel;
import gent.timdemey.cards.logging.LogManager;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.delta.IChangeTracker;
import gent.timdemey.cards.model.delta.StateChangeTracker;
import gent.timdemey.cards.model.entities.commands.CommandFactory;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.model.entities.state.StateFactory;
import gent.timdemey.cards.server.StartServer;
import gent.timdemey.cards.services.action.ActionService;
import gent.timdemey.cards.services.animation.AnimationDescriptorFactory;
import gent.timdemey.cards.services.animation.AnimationService;
import gent.timdemey.cards.services.config.ConfigService;
import gent.timdemey.cards.services.context.CommandNetworkService;
import gent.timdemey.cards.services.context.ContextService;
import gent.timdemey.cards.services.context.ICommandExecutor;
import gent.timdemey.cards.services.context.ServerCommandExecutor;
import gent.timdemey.cards.services.context.UICommandExecutor;
import static gent.timdemey.cards.services.contract.descriptors.PanelDescriptors.StartServer;
import gent.timdemey.cards.services.file.FileService;
import gent.timdemey.cards.services.frame.FrameService;
import gent.timdemey.cards.services.interfaces.IActionService;
import gent.timdemey.cards.services.interfaces.IAnimationDescriptorFactory;
import gent.timdemey.cards.services.interfaces.IAnimationService;
import gent.timdemey.cards.services.interfaces.IConfigurationService;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFileService;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.INetworkService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.IResourceCacheService;
import gent.timdemey.cards.services.interfaces.IResourceNameService;
import gent.timdemey.cards.services.interfaces.IResourceRepository;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.services.interfaces.ISoundService;
import gent.timdemey.cards.services.resources.ResourceCacheService;
import gent.timdemey.cards.services.resources.ResourceNameService;
import gent.timdemey.cards.services.resources.ResourceRepository;
import gent.timdemey.cards.services.sound.SoundService;
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
        // based on args we could also start a standalone server
        startUI(args);
    }
    
    public static void startUI(String[] args)
    {
        ICardPlugin plugin = getCardPlugin(args);
        
        Container container = new Container();          
        installPlugin(container, plugin);
        installCommonServices(container);    
        installUIServices(container);
        plugin.installCommonServices(container);
        plugin.installUIServices(container);
        
        State state = container.Get(StateFactory.class).CreateState();
        container.AddSingleton(State.class, state);
        
        StartUI startUI = container.Get(StartUI.class);        
        startUI.startUI();
    }
    
    public static void startServer(String[] args)
    {
        ICardPlugin plugin = getCardPlugin(args);
        
        Container container = new Container();          
        installPlugin(container, plugin);
        installCommonServices(container);    
        installServerServices(container);
        plugin.installCommonServices(container);
        
        State state = container.Get(StateFactory.class).CreateState();
        container.AddSingleton(State.class, state);
        
        StartServer startServer = container.Get(StartServer.class);        
        startServer.startServer();
    }

    private static ICardPlugin getCardPlugin(String[] args)
    {
        
        Container bootContainer = CreateBootContainer();
        Logger bootLogger = bootContainer.Get(Logger.class);
        
         // determine plugin and if found, let it install services
        ICardPlugin plugin = loadCardPlugin(args, bootLogger);
        if(plugin == null)
        {
            throw new IllegalStateException("Cannot load plugin class. Terminating.");
        }
        
        return plugin;
        
    }
    
    private static ICardPlugin loadCardPlugin(String[] args, Logger logger)
    {
        if(args.length != 1)
        {
            logger.error("A single argument is expected, but %s were given.", args.length);
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
            logger.error("The given class '%s' is not found in the classpath.", clazzName);
            return null;
        }

        if(!ICardPlugin.class.isAssignableFrom(clazz))
        {
            logger.error("Should provide a card plugin. The given class does not derive from ''.", ICardPlugin.class.getSimpleName());
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
            logger.error("The given plugin class cannot be instantiated", e);
            return null;
        }

        return plugin;
    }

    private static Container CreateBootContainer()
    {
        Container container = new Container();
        
        container.AddSingleton(ILogManager.class, new LogManager(LogLevel.DEBUG));                
        
        return container;
    }    

    private static void installPlugin(Container container, ICardPlugin plugin)
    {
        container.AddSingleton(ICardPlugin.class, plugin);
    }
    
    private static void installCommonServices(Container c)
    {        
        c.AddSingleton(IChangeTracker.class, StateChangeTracker.class);
        c.AddSingleton(IConfigurationService.class, ConfigService.class);
        c.AddSingleton(IContextService.class, ContextService.class);        
        c.AddSingleton(ILogManager.class, new LogManager(LogLevel.DEBUG));     
        c.AddSingleton(INetworkService.class, CommandNetworkService.class);    
    }
    
    private static void installUIServices(Container c)
    {        
        c.AddSingleton(IActionService.class, ActionService.class);
        c.AddSingleton(IAnimationDescriptorFactory.class, AnimationDescriptorFactory.class);
        c.AddSingleton(IAnimationService.class, AnimationService.class);  
        c.AddSingleton(ICommandExecutor.class, UICommandExecutor.class);
        c.AddSingleton(IFileService.class, FileService.class);
        c.AddSingleton(IFrameService.class, FrameService.class); 
        c.AddSingleton(IPanelService.class, PanelService.class);
        c.AddSingleton(IResourceCacheService.class, ResourceCacheService.class);
        c.AddSingleton(IResourceRepository.class, ResourceRepository.class);
        c.AddSingleton(IResourceNameService.class, ResourceNameService.class);   
        c.AddSingleton(IScalingService.class, ScalingService.class);   
        c.AddSingleton(ISoundService.class, SoundService.class);
    }
    
    private static void installServerServices(Container c)
    {        
        c.AddSingleton(IActionService.class, ActionService.class);
        c.AddSingleton(IAnimationDescriptorFactory.class, AnimationDescriptorFactory.class);
        c.AddSingleton(IAnimationService.class, AnimationService.class);  
        c.AddSingleton(IFileService.class, FileService.class);
        c.AddSingleton(IFrameService.class, FrameService.class); 
        c.AddSingleton(IPanelService.class, PanelService.class);
        c.AddSingleton(IResourceCacheService.class, ResourceCacheService.class);
        c.AddSingleton(IResourceRepository.class, ResourceRepository.class);
        c.AddSingleton(IResourceNameService.class, ResourceNameService.class);   
        c.AddSingleton(IScalingService.class, ScalingService.class);   
        c.AddSingleton(ISoundService.class, SoundService.class);
        c.AddSingleton(ICommandExecutor.class, ServerCommandExecutor.class);
    }
}
