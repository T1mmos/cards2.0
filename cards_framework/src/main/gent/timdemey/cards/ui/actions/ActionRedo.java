package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.entities.AGameEventAdapter;
import gent.timdemey.cards.entities.IGameOperations;
import gent.timdemey.cards.localization.Loc;

public class ActionRedo extends AAction {
    
    private class GameStartListener extends AGameEventAdapter
    {
        @Override
        public void onUndoRedoChanged() {
            checkEnabled();
        }
    }
    
    public ActionRedo() {
        super(ACTION_REDO, Loc.get("menuitem_redo"));
        Services.get(IGameOperations.class).addGameEventListener(new GameStartListener());
        checkEnabled();
    }    
}
