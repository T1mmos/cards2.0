package gent.timdemey.cards;

import gent.timdemey.cards.common.Version;
import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.CommandFactory;
import gent.timdemey.cards.model.entities.commands.SolShowCommandFactory;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.serialization.mappers.SolShowCommandDtoMapper;
import gent.timdemey.cards.services.animation.SolShowAnimationDescriptorFactory;
import gent.timdemey.cards.services.cardgame.SolShowCardGameService;
import gent.timdemey.cards.services.interfaces.IAnimationDescriptorFactory;
import gent.timdemey.cards.services.interfaces.ICardGameService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.interfaces.IResourceRepository;
import gent.timdemey.cards.services.resources.SolShowResourceRepository;
import gent.timdemey.cards.ui.panels.SolShowPanelService;
import gent.timdemey.cards.ui.panels.game.SolShowPositionService;

public class SolShowPlugin implements ICardPlugin
{
    public static void main(String[] args)
    {
        Main.main(new String [] { SolShowPlugin.class.getName() });
    }
    
    @Override
    public void installCommonServices(Container c)
    {
        c.AddSingleton(CommandFactory.class, SolShowCommandFactory.class);
        c.AddSingleton(CommandDtoMapper.class, SolShowCommandDtoMapper.class);
        c.AddSingleton(ICardGameService.class, SolShowCardGameService.class);
        c.AddSingleton(IPositionService.class, SolShowPositionService.class);        
    }
    
    @Override
    public void installUIServices(Container c)
    {        
        c.AddSingleton(IPanelService.class, SolShowPanelService.class);
        c.AddSingleton(IResourceRepository.class, SolShowResourceRepository.class);
        c.AddSingleton(IAnimationDescriptorFactory.class, SolShowAnimationDescriptorFactory.class);
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
