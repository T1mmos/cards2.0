package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.entities.AGameEventAdapter;
import gent.timdemey.cards.entities.IGameOperations;
import gent.timdemey.cards.localization.Loc;

public class ActionUndo extends AAction  {            
    
    private class GameStartListener extends AGameEventAdapter
    {
        @Override
        public void onUndoRedoChanged() {
            checkEnabled();
        }
    }
        
    public ActionUndo() {
        super(ACTION_UNDO, Loc.get("menuitem_undo"));
        Services.get(IGameOperations.class).addGameEventListener(new GameStartListener());
        checkEnabled();
    }
}
