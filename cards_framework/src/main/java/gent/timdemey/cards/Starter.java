package gent.timdemey.cards;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.di.ContainerService;
import gent.timdemey.cards.di.IContainerService;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.logging.LogLevel;
import gent.timdemey.cards.logging.LogManager;
import gent.timdemey.cards.model.delta.IChangeTracker;
import gent.timdemey.cards.model.delta.StateChangeTracker;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.model.entities.state.StateFactory;
import gent.timdemey.cards.server.StartServer;
import gent.timdemey.cards.services.action.ActionService;
import gent.timdemey.cards.services.animation.AnimationDescriptorFactory;
import gent.timdemey.cards.services.animation.AnimationService;
import gent.timdemey.cards.services.config.ConfigService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.context.ICommandExecutor;
import gent.timdemey.cards.services.context.ServerCommandExecutor;
import gent.timdemey.cards.services.context.UICommandExecutor;
import gent.timdemey.cards.services.file.FileService;
import gent.timdemey.cards.services.frame.FrameService;
import gent.timdemey.cards.services.interfaces.IActionService;
import gent.timdemey.cards.services.interfaces.IAnimationDescriptorFactory;
import gent.timdemey.cards.services.interfaces.IAnimationService;
import gent.timdemey.cards.services.interfaces.IConfigurationService;
import gent.timdemey.cards.services.interfaces.IFileService;
import gent.timdemey.cards.services.interfaces.IFrameService;
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

/**
 *
 * @author Timmos
 */
public class Starter
{
    private final IContainerService _ContainerService;
    private final ICardPlugin _CardPlugin;
    
    public Starter (IContainerService containerService, ICardPlugin cardPlugin)
    {
        this._ContainerService = containerService;
        this._CardPlugin = cardPlugin;
    }
    
    public void startUI()
    {        
        Container container = _ContainerService.create(ContextType.UI);
        
        installCommonServices(container);    
        installUIServices(container);
        _CardPlugin.installCommonServices(container);
        _CardPlugin.installUIServices(container);
        
        State state = container.Get(StateFactory.class).CreateState();
        container.AddSingleton(State.class, state);
        container.AddSingleton(ContextType.class, ContextType.UI);
        container.AddSingleton(Context.class, Context.class);
        
        StartUI startUI = container.Get(StartUI.class);        
        startUI.startUI();
    }
    
    public void startServer()
    {
        Container container = _ContainerService.create(ContextType.Server);  
        installCommonServices(container);   
        installServerServices(container);
        _CardPlugin.installCommonServices(container);
                
        State state = container.Get(StateFactory.class).CreateState();
        container.AddSingleton(State.class, state);
        container.AddSingleton(ContextType.class, ContextType.Server);
        container.AddSingleton(Context.class, Context.class);
        
        StartServer startServer = container.Get(StartServer.class);        
        startServer.startServer();
    }
        
    private void installCommonServices(Container container)
    {
        container.AddSingleton(ILogManager.class, new LogManager(LogLevel.DEBUG));  
        container.AddSingleton(IContainerService.class, _ContainerService);
        container.AddSingleton(ICardPlugin.class, _CardPlugin);
    }
    
    private static void installUIServices(Container c)
    {            
        c.AddSingleton(IActionService.class, ActionService.class);
        c.AddSingleton(IAnimationDescriptorFactory.class, AnimationDescriptorFactory.class);
        c.AddSingleton(IAnimationService.class, AnimationService.class);  
        c.AddSingleton(IChangeTracker.class, StateChangeTracker.class);
        c.AddSingleton(IConfigurationService.class, ConfigService.class);
        c.AddSingleton(ICommandExecutor.class, UICommandExecutor.class);
        c.AddSingleton(IContainerService.class, ContainerService.class);    
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
        c.AddSingleton(IChangeTracker.class, StateChangeTracker.class);
        c.AddSingleton(IConfigurationService.class, ConfigService.class);
        c.AddSingleton(IContainerService.class, ContainerService.class);   
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
