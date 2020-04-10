package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;

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
        super(AAction.ACTION_CREATE, Loc.get(LocKey.Menu_creategame));
    }
}
