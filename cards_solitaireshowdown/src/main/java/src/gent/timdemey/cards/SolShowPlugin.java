package gent.timdemey.cards;

import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.serialization.SolShowSerializationService;
import gent.timdemey.cards.services.cardgame.SolShowCardGameService;
import gent.timdemey.cards.services.commands.SolShowCommandService;
import gent.timdemey.cards.services.id.SolShowIdService;
import gent.timdemey.cards.services.interfaces.IAnimationService;
import gent.timdemey.cards.services.interfaces.ICardGameService;
import gent.timdemey.cards.services.interfaces.ICommandService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.interfaces.IResourceRepository;
import gent.timdemey.cards.services.interfaces.ISerializationService;
import gent.timdemey.cards.services.interfaces.ISolShowIdService;
import gent.timdemey.cards.services.panels.SolShowAnimationService;
import gent.timdemey.cards.services.panels.SolShowGamePanelStateListener;
import gent.timdemey.cards.services.panels.SolShowPanelService;
import gent.timdemey.cards.services.panels.SolShowPositionService;
import gent.timdemey.cards.services.resources.SolShowResourceRepository;

public class SolShowPlugin implements ICardPlugin
{
    @Override
    public void installServices()
    {
        App.getServices().install(ICommandService.class, new SolShowCommandService());
        App.getServices().install(ICardGameService.class, new SolShowCardGameService());
        App.getServices().install(ISerializationService.class, new SolShowSerializationService());
        App.getServices().install(ISolShowIdService.class, new SolShowIdService());
        App.getServices().install(IPanelService.class, new SolShowPanelService());
        App.getServices().install(IAnimationService.class, new SolShowAnimationService());
        App.getServices().install(IResourceRepository.class, new SolShowResourceRepository());
    }

    @Override
    public void installUiServices()
    {
        App.getServices().install(IPositionService.class, new SolShowPositionService());
        App.getServices().install(IStateListener.class, new SolShowGamePanelStateListener());
    }

    @Override
    public String getName()
    {
        return "Solitaire Showdown";
    }

    @Override
    public int getMajorVersion()
    {
        return 2;
    }

    @Override
    public int getMinorVersion()
    {
        return 0;
    }

    @Override
    public int getPlayerCount()
    {
        return 2;
    }

    @Override
    public State createState()
    {
        return new State();
    }
}
