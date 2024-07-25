package gent.timdemey.cards;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.di.ContainerBuilder;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.logging.LogManager;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.serialization.SerializationService;
import gent.timdemey.cards.services.action.ActionService;
import gent.timdemey.cards.services.animation.AnimationDescriptorFactory;
import gent.timdemey.cards.services.animation.AnimationService;
import gent.timdemey.cards.services.config.ConfigService;
import gent.timdemey.cards.services.context.CommandNetworkService;
import gent.timdemey.cards.services.context.ContextService;
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
import gent.timdemey.cards.services.interfaces.ISerializationService;
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
        Container bootContainer = CreateBootContainer();
        Logger bootLogger = bootContainer.Get(Logger.class);
        
        ContainerBuilder cb = new ContainerBuilder();        
        
        installBaseServices(cb);                  
        installCardPlugin(args, cb, bootLogger);

        Container container = cb.Build();
        
        StartUI startUI = container.Get(StartUI.class);
        
        
        SwingUtilities.invokeLater(startUI::startUI);
    }

    private static void installCardPlugin(String[] args, ContainerBuilder cb, Logger logger) {
        
         // determine plugin and if found, let it install services
        ICardPlugin plugin = loadCardPlugin(args, logger);
        if(plugin == null)
        {
            throw new IllegalStateException("Cannot load plugin class. Terminating.");
        }
        
        plugin.installServices(cb);
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
        ContainerBuilder cb = new ContainerBuilder();
        
        cb.AddSingleton(ILogManager.class, LogManager.class);                
        
        return cb.Build();
    }
    
    private static void installBaseServices(ContainerBuilder cb)
    {
        cb.AddSingleton(ILogManager.class, LogManager.class);                
        cb.AddSingleton(IConfigurationService.class, ConfigService.class);
        cb.AddSingleton(IContextService.class, ContextService.class);
        cb.AddSingleton(IScalingService.class, ScalingService.class);
        cb.AddSingleton(IResourceCacheService.class, ResourceCacheService.class);
        cb.AddSingleton(IResourceRepository.class, ResourceRepository.class);
        cb.AddSingleton(IResourceNameService.class, ResourceNameService.class);
        cb.AddSingleton(ISerializationService.class, SerializationService.class);
        cb.AddSingleton(INetworkService.class, CommandNetworkService.class);        
        cb.AddSingleton(IAnimationService.class, AnimationService.class); 
        cb.AddSingleton(IFrameService.class, FrameService.class);
        cb.AddSingleton(IActionService.class, ActionService.class);
        cb.AddSingleton(IPanelService.class, PanelService.class);
        cb.AddSingleton(IFileService.class, FileService.class);
        cb.AddSingleton(ISoundService.class, SoundService.class);
        cb.AddSingleton(IAnimationDescriptorFactory.class, AnimationDescriptorFactory.class);
        cb.AddTransient(State.class, State.class);
    }
}
