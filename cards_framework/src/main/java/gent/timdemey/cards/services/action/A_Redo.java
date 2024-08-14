package gent.timdemey.cards.services.action;

import gent.timdemey.cards.readonlymodel.ChangeList;
import gent.timdemey.cards.readonlymodel.ReadOnlyCommandHistory;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptor;
import gent.timdemey.cards.services.interfaces.IActionService;

class A_Redo extends ActionBase
{
    protected A_Redo(IActionService actionService, ActionDescriptor desc, String title)
    {
        super(actionService, desc, title);
    }

    @Override
    public void onChanges(ChangeList changeList)
    {
        if (changeList.HasAnyChange(ReadOnlyCommandHistory.CurrentIndex, ReadOnlyState.CommandHistory))
        {
            checkEnabled();
        }
    }
}
