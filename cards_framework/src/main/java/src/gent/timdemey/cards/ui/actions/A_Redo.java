package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyCommandHistory;
import gent.timdemey.cards.readonlymodel.ReadOnlyProperty;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.context.ContextType;

public class A_Redo extends ActionBase
{
    protected A_Redo(ActionDescriptor desc, String title)
    {
        super(desc, title);
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
