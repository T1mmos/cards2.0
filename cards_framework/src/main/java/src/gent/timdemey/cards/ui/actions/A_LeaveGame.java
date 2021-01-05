package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyProperty;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptor;

class A_LeaveGame extends ActionBase
{
    protected A_LeaveGame(ActionDescriptor desc, String title)
    {
        super(desc, title);
    }

    @Override
    public void onChange(ReadOnlyChange roChange)
    {
        ReadOnlyProperty<?> property = roChange.property;

        if (property == ReadOnlyState.GameState)
        {
            checkEnabled();
        }
    }
}
