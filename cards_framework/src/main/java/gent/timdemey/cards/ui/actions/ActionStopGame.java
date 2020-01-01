package gent.timdemey.cards.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.readonlymodel.AGameEventAdapter;
import gent.timdemey.cards.services.IGameOperationsService;

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
        Services.get(IGameOperationsService.class).addGameEventListener(new GameStartListener());
        check();
    }
    
    private void check()
    {
        setEnabled(Services.get(IGameOperationsService.class).canStopGame());
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Services.get(IGameOperationsService.class).stopGame();        
    }
}
