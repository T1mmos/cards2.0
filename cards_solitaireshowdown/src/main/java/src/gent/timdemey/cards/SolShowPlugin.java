package gent.timdemey.cards;

import gent.timdemey.cards.services.ICardGameCreationService;
import gent.timdemey.cards.services.ICommandService;
import gent.timdemey.cards.services.IGamePanelManager;
import gent.timdemey.cards.services.IPositionManager;
import gent.timdemey.cards.services.ISerializationService;
import gent.timdemey.cards.services.boot.SolShowCardGameCreationService;
import gent.timdemey.cards.services.commands.SolShowCommandService;
import gent.timdemey.cards.services.gamepanel.SolShowGamePanelManager;
import gent.timdemey.cards.services.position.SolShowPositionManager;
import gent.timdemey.cards.services.serialization.SolShowSerializationService;

public class SolShowPlugin implements ICardPlugin
{
    @Override
    public void installServices()
    {
        App.services.install(ICommandService.class, new SolShowCommandService());
        App.services.install(IPositionManager.class, new SolShowPositionManager());
        App.services.install(ICardGameCreationService.class, new SolShowCardGameCreationService());
        App.services.install(IGamePanelManager.class, new SolShowGamePanelManager());
        App.services.install(ISerializationService.class, new SolShowSerializationService());
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
}
