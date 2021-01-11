package gent.timdemey.cards.services.action;

import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptor;

class A_StartMP extends ActionBase
{
    protected A_StartMP(ActionDescriptor desc, String title)
    {
        super(desc, title);
    }

    @Override
    public void onChange(ReadOnlyChange roChange)
    {
        if (roChange.property == ReadOnlyState.Players)
        {
            checkEnabled();
        }
    }
}
