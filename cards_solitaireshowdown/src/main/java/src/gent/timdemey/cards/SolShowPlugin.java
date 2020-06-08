package gent.timdemey.cards;

import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.serialization.SolShowSerializationService;
import gent.timdemey.cards.services.cardgame.SolShowCardGameService;
import gent.timdemey.cards.services.commands.SolShowCommandService;
import gent.timdemey.cards.services.gamepanel.SolShowGamePanelService;
import gent.timdemey.cards.services.gamepanel.SolShowPositionManager;
import gent.timdemey.cards.services.interfaces.ICardGameService;
import gent.timdemey.cards.services.interfaces.ICommandService;
import gent.timdemey.cards.services.interfaces.IGamePanelService;
import gent.timdemey.cards.services.interfaces.IPositionManager;
import gent.timdemey.cards.services.interfaces.ISerializationService;

public class SolShowPlugin implements ICardPlugin
{
    @Override
    public void installServices()
    {
        App.getServices().install(ICommandService.class, new SolShowCommandService());
        App.getServices().install(ICardGameService.class, new SolShowCardGameService());
        App.getServices().install(ISerializationService.class, new SolShowSerializationService());
    }

    @Override
    public void installUiServices()
    {
        App.getServices().install(IPositionManager.class, new SolShowPositionManager());
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
