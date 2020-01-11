package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.localization.Loc;

/**
 * Action to start hosting / creating a server.
 * 
 * @author Timmos
 *
 */
public class ActionCreateGame extends AAction
{

    public ActionCreateGame()
    {
        super(AAction.ACTION_CREATE, Loc.get("menuitem_creategame"));
    }
}
