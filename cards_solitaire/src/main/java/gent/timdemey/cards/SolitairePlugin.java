package gent.timdemey.cards;

import gent.timdemey.cards.services.ICardGameCreationService;
import gent.timdemey.cards.services.ICommandService;
import gent.timdemey.cards.services.IGamePanelManager;
import gent.timdemey.cards.services.IPositionManager;
import gent.timdemey.cards.services.boot.SolitaireCardGameCreationService;
import gent.timdemey.cards.services.commands.SolitaireCommandService;
import gent.timdemey.cards.services.gamepanel.SolitaireGamePanelManager;
import gent.timdemey.cards.services.position.SolitairePositionManager;

public class SolitairePlugin implements ICardPlugin {

    @Override
    public void installServices() {
        Services.install(ICommandService.class, new SolitaireCommandService());
        Services.install(IPositionManager.class, new SolitairePositionManager());
        Services.install(ICardGameCreationService.class, new SolitaireCardGameCreationService());
        Services.install(IGamePanelManager.class, new SolitaireGamePanelManager());
    }

    @Override
    public String getName() {
        return "Solitaire";
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 6;
    }

    @Override
    public int getPlayerCount() {
        // TODO Auto-generated method stub
        return 1;
    }
}
