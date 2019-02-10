package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.entities.AGameEventAdapter;
import gent.timdemey.cards.entities.IGameOperations;
import gent.timdemey.cards.localization.Loc;

public class ActionStartGame extends AAction {

    private class GameStartListener extends AGameEventAdapter
    {
        @Override
        public void onStartGame() {
            checkEnabled();
        }

        @Override
        public void onStopGame() {
            checkEnabled();
        }
    }
    
    
    public ActionStartGame() {
        super(AAction.ACTION_START, Loc.get("menuitem_newgame"));
        Services.get(IGameOperations.class).addGameEventListener(new GameStartListener());
        checkEnabled();
    }
}
