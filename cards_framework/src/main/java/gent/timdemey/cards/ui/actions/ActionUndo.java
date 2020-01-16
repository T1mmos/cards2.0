package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.readonlymodel.AGameEventAdapter;
import gent.timdemey.cards.services.IGameOperationsService;

public class ActionUndo extends AAction
{

    private class GameStartListener extends AGameEventAdapter
    {
        @Override
        public void onUndoRedoChanged()
        {
            checkEnabled();
        }
    }

    public ActionUndo()
    {
        super(ACTION_UNDO, Loc.get("menuitem_undo"));
        Services.get(IGameOperationsService.class).addGameEventListener(new GameStartListener());
        checkEnabled();
    }
}
