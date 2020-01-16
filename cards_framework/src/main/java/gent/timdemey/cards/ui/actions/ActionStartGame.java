package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.readonlymodel.AGameEventAdapter;
import gent.timdemey.cards.services.IGameOperationsService;

public class ActionStartGame extends AAction
{

    private class GameStartListener extends AGameEventAdapter
    {
        @Override
        public void onStartGame()
        {
            checkEnabled();
        }

        @Override
        public void onStopGame()
        {
            checkEnabled();
        }
    }

    public ActionStartGame()
    {
        super(AAction.ACTION_START, Loc.get("menuitem_newgame"));
        Services.get(IGameOperationsService.class).addGameEventListener(new GameStartListener());
        checkEnabled();
    }
}
