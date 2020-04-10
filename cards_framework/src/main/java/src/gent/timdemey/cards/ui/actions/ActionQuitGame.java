package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;

public class ActionQuitGame extends AAction
{

    public ActionQuitGame()
    {
        super(AAction.ACTION_QUIT, Loc.get(LocKey.Menu_quit));
    }
}
