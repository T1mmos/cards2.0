package gent.timdemey.cards.services.action;

import gent.timdemey.cards.readonlymodel.ChangeList;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptor;
import gent.timdemey.cards.services.interfaces.IActionService;

class A_LeaveGame extends ActionBase
{
    protected A_LeaveGame(IActionService actionService, ActionDescriptor desc, String title)
    {
        super(actionService, desc, title);
    }

    @Override
    public void onChanges(ChangeList changeList)
    {
        changeList.OnChange(ReadOnlyState.GameState, this::checkEnabled);
    }
}
