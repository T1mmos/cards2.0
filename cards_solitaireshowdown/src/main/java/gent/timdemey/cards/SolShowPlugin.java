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
import gent.timdemey.cards.ui.actions.IActionService;
import gent.timdemey.cards.ui.actions.SolShowActionService;

public class SolShowPlugin implements ICardPlugin
{
    @Override
    public void installServices()
    {
        Services.install(ICommandService.class, new SolShowCommandService());
        Services.install(IPositionManager.class, new SolShowPositionManager());
        Services.install(ICardGameCreationService.class, new SolShowCardGameCreationService());
        Services.install(IGamePanelManager.class, new SolShowGamePanelManager());
        Services.install(IActionService.class, new SolShowActionService());
        Services.install(ISerializationService.class, new SolShowSerializationService());
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
