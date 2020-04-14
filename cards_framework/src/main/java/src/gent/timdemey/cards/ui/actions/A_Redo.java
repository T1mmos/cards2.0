package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyCommandHistory;
import gent.timdemey.cards.readonlymodel.ReadOnlyProperty;
import gent.timdemey.cards.services.IContextService;

public class A_Redo extends ActionBase
{
    private class RedoListener implements IStateListener
    {

        @Override
        public void onChange(ReadOnlyChange change)
        {
            ReadOnlyProperty<?> property = change.property;

            if (property == ReadOnlyCommandHistory.CurrentIndex)
            {
                checkEnabled();
            }
        }
    }

    public A_Redo()
    {
        super(Actions.ACTION_REDO, Loc.get(LocKey.Menu_redo));
        Services.get(IContextService.class).getThreadContext().addStateListener(new RedoListener());
        checkEnabled();
    }
}
