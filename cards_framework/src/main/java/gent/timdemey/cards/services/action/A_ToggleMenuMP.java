package gent.timdemey.cards.services.action;

import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyProperty;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptor;

public class A_ToggleMenuMP extends ActionBase
{
    protected A_ToggleMenuMP(ActionDescriptor desc, String title)
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
