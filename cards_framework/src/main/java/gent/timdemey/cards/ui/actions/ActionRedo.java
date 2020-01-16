package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.readonlymodel.AGameEventAdapter;
import gent.timdemey.cards.services.IGameOperationsService;

public class ActionRedo extends AAction
{

    private class GameStartListener extends AGameEventAdapter
    {
        @Override
        public void onUndoRedoChanged()
        {
            checkEnabled();
        }
    }

    public ActionRedo()
    {
        super(ACTION_REDO, Loc.get("menuitem_redo"));
        Services.get(IGameOperationsService.class).addGameEventListener(new GameStartListener());
        checkEnabled();
    }
}
