package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.IContextService;

public class A_StartMultiplayerGame extends ActionBase
{
    private class PlayerListener implements IStateListener
    {

        @Override
        public void onChange(ReadOnlyChange change)
        {
            if (change.property == ReadOnlyState.Players)
            {
                checkEnabled();
            }
        }
        
    }

    protected A_StartMultiplayerGame()
    {
        super(Actions.ACTION_STARTMULTIPLAYER, Loc.get(LocKey.Button_startMultiplayerGame));
        Services.get(IContextService.class).getThreadContext().addStateListener(new PlayerListener());
        checkEnabled();
    }

}
