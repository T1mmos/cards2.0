package gent.timdemey.cards;

import gent.timdemey.cards.di.Container;
import java.lang.reflect.InvocationTargetException;

import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.di.ContainerService;
import gent.timdemey.cards.di.IContainerService;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.logging.LogLevel;
import gent.timdemey.cards.logging.LogManager;

public class Main
{
    private Main()
    {
    }

    public static void main(String[] args)
    {       
        // create a basic container with minimal registrations
        Container container = new Container();
        container.AddSingleton(IContainerService.class, new ContainerService());
        container.AddSingleton(ILogManager.class, new LogManager(LogLevel.DEBUG));  
              
        // set plugin
        ICardPlugin plugin = getCardPlugin(container, args);
        container.AddSingleton(ICardPlugin.class, plugin);  
        
        Starter starter = container.Get(Starter.class);
        starter.startUI();
    }
    
    
    private static ICardPlugin getCardPlugin(Container rootContainer, String[] args)
    {
        Logger bootLogger = rootContainer.Get(Logger.class);
        
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
}
