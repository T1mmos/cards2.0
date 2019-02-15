package gent.timdemey.cards;

import gent.timdemey.cards.entities.ICardGameCreator;
import gent.timdemey.cards.entities.IGameOperations;
import gent.timdemey.cards.entities.SolShowCardGameCreator;
import gent.timdemey.cards.entities.SolShowGameOperations;
import gent.timdemey.cards.gamepanel.SolShowGamePanelManager;
import gent.timdemey.cards.position.SolShowPositionManager;
import gent.timdemey.cards.services.gamepanel.IGamePanelManager;
import gent.timdemey.cards.services.plugin.IPositionManager;
import gent.timdemey.cards.ui.actions.IActionService;
import gent.timdemey.cards.ui.actions.SolShowActionService;

public class SolShowPlugin implements ICardPlugin {
    @Override
    public void installServices() {
        Services.install(IGameOperations.class, new SolShowGameOperations());
        Services.install(IPositionManager.class, new SolShowPositionManager());
        Services.install(ICardGameCreator.class, new SolShowCardGameCreator());
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
