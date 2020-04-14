package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;

public class A_QuitGame extends ActionBase
{
    public A_QuitGame()
    {
        super(Actions.ACTION_QUIT, Loc.get(LocKey.Menu_quit));
    }
}
