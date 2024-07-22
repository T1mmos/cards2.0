package gent.timdemey.cards;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.di.ContainerBuilder;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.logging.LogLevel;
import gent.timdemey.cards.logging.LogManager;
import gent.timdemey.cards.logging.Logger;
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
        ContainerBuilder cb = new ContainerBuilder();        
        
        installBaseServices(cb);  
        installCardPlugin(args, cb);

        Container container = cb.Build();
        
        StartUI startUI = container.Get(StartUI.class);
        
        
        SwingUtilities.invokeLater(startUI::startUI);
    }

    private static void installCardPlugin(String[] args, ContainerBuilder cb) {
        
         // determine plugin and if found, let it install services
        ICardPlugin plugin = loadCardPlugin(args);
        if(plugin == null)
        {
            throw new IllegalStateException("Cannot load plugin class. Terminating.");
        }
        
        plugin.installServices(cb);
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

    private static void installBaseServices(ContainerBuilder cb)
    {
        cb.AddTransient(ILogManager.class, LogManager.class);                
        cb.AddSingleton(IConfigurationService.class, new ConfigService());
        cb.AddSingleton(IContextService.class, new ContextService());
        cb.AddSingleton(IScalingService.class, new ScalingService());
        cb.AddSingleton(IResourceCacheService.class, new ResourceCacheService());
        cb.AddSingleton(IResourceRepository.class, new ResourceRepository());
        cb.AddSingleton(IResourceNameService.class, new ResourceNameService());
        cb.AddSingleton(ISerializationService.class, new SerializationService());
        cb.AddSingleton(INetworkService.class, new CommandNetworkService());        
        cb.AddSingleton(IAnimationService.class, new AnimationService()); 
        cb.AddSingleton(IFrameService.class, new FrameService());
        cb.AddSingleton(IActionService.class, new ActionService());
        cb.AddSingleton(IPanelService.class, new PanelService());
        cb.AddSingleton(IFileService.class, new FileService());
        cb.AddSingleton(ISoundService.class, new SoundService());
        cb.AddSingleton(IAnimationDescriptorFactory.class, new AnimationDescriptorFactory());
    }
}
