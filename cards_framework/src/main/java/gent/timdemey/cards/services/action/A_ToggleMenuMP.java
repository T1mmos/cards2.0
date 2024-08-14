package gent.timdemey.cards.services.action;

import gent.timdemey.cards.readonlymodel.ChangeList;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptor;
import gent.timdemey.cards.services.interfaces.IActionService;

public class A_ToggleMenuMP extends ActionBase
{
    protected A_ToggleMenuMP(IActionService actionService, ActionDescriptor desc, String title)
    {
        super(actionService, desc, title);
    }

    @Override
    public void onChanges(ChangeList changeList)
    {        
        changeList.OnChange(ReadOnlyState.GameState, this::checkEnabled);
    }
}
