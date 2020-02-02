package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyCommandHistory;
import gent.timdemey.cards.services.IContextService;

public class ActionUndo extends AAction
{

    private class UndoListener implements IStateListener
    {

        @Override
        public void onChange(ReadOnlyChange change)
        {
            if (change.property == ReadOnlyCommandHistory.CurrentIndex)
            {
                checkEnabled();
            }
        }
    }

    public ActionUndo()
    {
        super(ACTION_UNDO, Loc.get("menuitem_undo"));
        checkEnabled();
        
        Services.get(IContextService.class).getThreadContext().addStateListener(new UndoListener());
        checkEnabled();
    }
}
