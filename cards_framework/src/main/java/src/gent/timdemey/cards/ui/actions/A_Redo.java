package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyCommandHistory;
import gent.timdemey.cards.readonlymodel.ReadOnlyProperty;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.context.ContextType;

public class A_Redo extends ActionBase
{
    protected A_Redo()
    {
        super(Actions.ACTION_REDO, Loc.get(LocKey.Menu_redo));
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
        ReadOnlyProperty<?> property = roChange.property;

        if (property == ReadOnlyCommandHistory.CurrentIndex || property == ReadOnlyState.CommandHistory)
        {
            checkEnabled();
        }
    }
}
