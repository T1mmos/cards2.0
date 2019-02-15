package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.localization.Loc;

public class ActionQuitGame extends AAction {
    
    public ActionQuitGame() {
        super(AAction.ACTION_QUIT, Loc.get("menuitem_quit"));
    }
}
