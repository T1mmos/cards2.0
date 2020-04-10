package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.IContextService;

public class ActionStopGame extends AAction
{
    private class GameStopListener implements IStateListener
    {

        @Override
        public void onChange(ReadOnlyChange change)
        {
            if (change.property == ReadOnlyState.CardGame)
            {
                checkEnabled();
            }
        }
    }

    public ActionStopGame()
    {
        super(AAction.ACTION_STOP, Loc.get(LocKey.Menu_stopgame));
        Services.get(IContextService.class).getThreadContext().addStateListener(new GameStopListener());
        checkEnabled();
    }
}
