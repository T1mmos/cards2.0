package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;

public class ActionJoinGame extends AAction
{
    
    
    public ActionJoinGame()
    {
        super(AAction.ACTION_JOIN, Loc.get(LocKey.Menu_join));
    }

}
