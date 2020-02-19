package gent.timdemey.cards;

import gent.timdemey.cards.position.SolShowPositionManager;
import gent.timdemey.cards.services.IGamePanelManager;
import gent.timdemey.cards.services.IPositionManager;
import gent.timdemey.cards.services.boot.SolShowCardGameCreationService;
import gent.timdemey.cards.services.commands.SolShowCommandService;
import gent.timdemey.cards.services.gamepanel.SolShowGamePanelManager;
import gent.timdemey.cards.ui.actions.IActionService;
import gent.timdemey.cards.ui.actions.SolShowActionService;

public class SolShowPlugin implements ICardPlugin {
    @Override
    public void installServices() {
        Services.install(ICommandService.class, new SolShowCommandService());
        Services.install(IPositionManager.class, new SolShowPositionManager());
        Services.install(ICardGameCreator.class, new SolShowCardGameCreationService());
        Services.install(IGamePanelManager.class, new SolShowGamePanelManager());
        Services.install(IActionService.class, new SolShowActionService());
    }

    @Override
    public String getName() { 
        return "Solitaire Showdown"; 
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 1;
    }

    @Override
    public int getPlayerCount() {
        return 2;
    }
}
