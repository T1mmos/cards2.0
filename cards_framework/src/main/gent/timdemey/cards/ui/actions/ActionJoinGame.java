package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.localization.Loc;

public class ActionJoinGame extends AAction {

    public ActionJoinGame() {
        super(AAction.ACTION_JOIN, Loc.get("menuitem_join"));
    }

}
