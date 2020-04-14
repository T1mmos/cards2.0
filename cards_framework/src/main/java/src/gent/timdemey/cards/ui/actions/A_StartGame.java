package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.IContextService;

public class A_StartGame extends ActionBase
{

    private class GameStartListener implements IStateListener
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

    public A_StartGame()
    {
        super(Actions.ACTION_START, Loc.get(LocKey.Menu_newgame));
        Services.get(IContextService.class).getThreadContext().addStateListener(new GameStartListener());
        checkEnabled();
    }
}
