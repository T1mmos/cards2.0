package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;

/**
 * Action to start hosting / creating a server.
 * 
 * @author Timmos
 *
 */
public class A_CreateGame extends ActionBase
{

    public A_CreateGame()
    {
        super(Actions.ACTION_CREATE, Loc.get(LocKey.Menu_creategame));
    }
}
