package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.localization.Loc;

public class ActionLeaveGame extends AAction
{

    public ActionLeaveGame()
    {
        super(AAction.ACTION_LEAVE, Loc.get("menuitem_leave"));
    }

}
