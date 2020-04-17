package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyCommandHistory;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.IContextService;

public class A_Undo extends ActionBase
{

    private class UndoListener implements IStateListener
    {

        @Override
        public void onChange(ReadOnlyChange change)
        {
            if (change.property == ReadOnlyCommandHistory.CurrentIndex || change.property == ReadOnlyState.CommandHistory)
            {
                checkEnabled();
            }
        }
    }

    protected A_Undo()
    {
        super(Actions.ACTION_UNDO, Loc.get(LocKey.Menu_undo));
        checkEnabled();
        
        Services.get(IContextService.class).getThreadContext().addStateListener(new UndoListener());
        checkEnabled();
    }
}
