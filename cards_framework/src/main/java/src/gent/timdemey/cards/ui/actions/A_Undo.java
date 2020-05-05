package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyCommandHistory;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.context.ContextType;

public class A_Undo extends ActionBase
{
    protected A_Undo()
    {
        super(Actions.ACTION_UNDO, Loc.get(LocKey.Menu_undo));
    }

    @Override
    public void onContextInitialized(ContextType type)
    {
    }

    @Override
    public void onContextDropped(ContextType type)
    {
    }

    @Override
    public void onChange(ReadOnlyChange roChange)
    {
        if (roChange.property == ReadOnlyCommandHistory.CurrentIndex || roChange.property == ReadOnlyState.CommandHistory)
        {
            checkEnabled();
        }        
    }
}
