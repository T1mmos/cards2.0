package gent.timdemey.cards;

import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.serialization.SolShowSerializationService;
import gent.timdemey.cards.services.cardgame.SolShowCardGameService;
import gent.timdemey.cards.services.commands.SolShowCommandService;
import gent.timdemey.cards.services.gamepanel.SolShowGamePanelService;
import gent.timdemey.cards.services.gamepanel.SolShowPositionManager;
import gent.timdemey.cards.services.id.SolShowIdService;
import gent.timdemey.cards.services.interfaces.ICardGameService;
import gent.timdemey.cards.services.interfaces.ICommandService;
import gent.timdemey.cards.services.interfaces.IGamePanelService;
import gent.timdemey.cards.services.interfaces.IIdService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.services.interfaces.ISerializationService;
import gent.timdemey.cards.services.scaleman.SolShowScalingService;

public class SolShowPlugin implements ICardPlugin
{
    @Override
    public void installServices()
    {
        App.getServices().install(ICommandService.class, new SolShowCommandService());
        App.getServices().install(ICardGameService.class, new SolShowCardGameService());
        App.getServices().install(ISerializationService.class, new SolShowSerializationService());
        App.getServices().install(IIdService.class, new SolShowIdService());
        App.getServices().install(IScalingService.class, new SolShowScalingService());
    }

    @Override
    public void installUiServices()
    {
        App.getServices().install(IPositionService.class, new SolShowPositionManager());
        App.getServices().install(IGamePanelService.class, new SolShowGamePanelService());
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
