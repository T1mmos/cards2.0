package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;

public class A_LeaveGame extends ActionBase
{

    protected A_LeaveGame()
    {
        super(Actions.ACTION_LEAVE, Loc.get(LocKey.Menu_leave));
    }

}
