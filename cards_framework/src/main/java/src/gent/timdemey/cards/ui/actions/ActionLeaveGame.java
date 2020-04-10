package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;

public class ActionLeaveGame extends AAction
{

    public ActionLeaveGame()
    {
        super(AAction.ACTION_LEAVE, Loc.get(LocKey.Menu_leave));
    }

}
