package gent.timdemey.cards;

import gent.timdemey.cards.entities.ICardGameCreator;
import gent.timdemey.cards.entities.IGameOperations;
import gent.timdemey.cards.entities.SolitaireCardGameCreator;
import gent.timdemey.cards.entities.SolitaireGameOperations;
import gent.timdemey.cards.gamepanel.SolitaireGamePanelManager;
import gent.timdemey.cards.position.SolitairePositionManager;
import gent.timdemey.cards.services.gamepanel.IGamePanelManager;
import gent.timdemey.cards.services.plugin.IPositionManager;

public class SolitairePlugin implements ICardPlugin {

    @Override
    public void installServices() {
        Services.install(IGameOperations.class, new SolitaireGameOperations());
        Services.install(IPositionManager.class, new SolitairePositionManager());
        Services.install(ICardGameCreator.class, new SolitaireCardGameCreator());
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
