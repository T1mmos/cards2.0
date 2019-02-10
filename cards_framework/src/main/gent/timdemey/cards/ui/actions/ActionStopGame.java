package gent.timdemey.cards.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.entities.AGameEventAdapter;
import gent.timdemey.cards.entities.IGameOperations;
import gent.timdemey.cards.localization.Loc;

public class ActionStopGame extends AbstractAction {
    private class GameStartListener extends AGameEventAdapter
    {
        @Override
        public void onStartGame() {
            check();
        }

        @Override
        public void onStopGame() {
            check();
        }
    }
    
    public ActionStopGame ()
    {
        super(Loc.get("menuitem_stopgame"));
        Services.get(IGameOperations.class).addGameEventListener(new GameStartListener());
        check();
    }
    
    private void check()
    {
        setEnabled(Services.get(IGameOperations.class).canStopGame());
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Services.get(IGameOperations.class).stopGame();        
    }
}
