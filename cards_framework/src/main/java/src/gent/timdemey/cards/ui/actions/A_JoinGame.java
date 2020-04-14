package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;

public class A_JoinGame extends ActionBase
{
    
    
    public A_JoinGame()
    {
        super(Actions.ACTION_JOIN, Loc.get(LocKey.Menu_join));
    }

}
