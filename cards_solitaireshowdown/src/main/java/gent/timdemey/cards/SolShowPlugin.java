package gent.timdemey.cards;

import gent.timdemey.cards.common.Version;
import gent.timdemey.cards.di.ContainerBuilder;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.serialization.SolShowSerializationService;
import gent.timdemey.cards.services.animation.SolShowAnimationDescriptorFactory;
import gent.timdemey.cards.services.cardgame.SolShowCardGameService;
import gent.timdemey.cards.services.commands.SolShowCommandService;
import gent.timdemey.cards.services.id.SolShowIdService;
import gent.timdemey.cards.services.interfaces.IAnimationDescriptorFactory;
import gent.timdemey.cards.services.interfaces.ICardGameService;
import gent.timdemey.cards.services.interfaces.ICommandService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.interfaces.IResourceRepository;
import gent.timdemey.cards.services.interfaces.ISerializationService;
import gent.timdemey.cards.services.interfaces.ISolShowIdService;
import gent.timdemey.cards.services.resources.SolShowResourceRepository;
import gent.timdemey.cards.ui.panels.SolShowPanelService;
import gent.timdemey.cards.ui.panels.game.SolShowPositionService;

public class SolShowPlugin implements ICardPlugin
{
    public static void main(String[] args)
    {
        Start.main(new String [] { SolShowPlugin.class.getName() });
    }
    
    @Override
    public void installServices(ContainerBuilder cb)
    {
        cb.AddSingleton(ICommandService.class, new SolShowCommandService());
        cb.AddSingleton(ICardGameService.class, new SolShowCardGameService());
        cb.AddSingleton(ISerializationService.class, new SolShowSerializationService());
        cb.AddSingleton(ISolShowIdService.class,  new SolShowIdService());
        cb.AddSingleton(IPanelService.class, new SolShowPanelService());
        cb.AddSingleton(IResourceRepository.class, new SolShowResourceRepository());
        cb.AddSingleton(IPositionService.class, new SolShowPositionService());
        cb.AddSingleton(IAnimationDescriptorFactory.class, new SolShowAnimationDescriptorFactory());
    }

    @Override
    public String getName()
    {
        return "Solitaire Showdown";
    }

    @Override
    public Version getVersion()
    {
        return new Version(1,0);
    }

    @Override
    public int getPlayerCount()
    {
        return 2;
    }
}
