package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.services.contract.descriptors.ActionDescriptor;

public interface IActionService
{
    public boolean canExecuteAction(ActionDescriptor desc);

    public void executeAction(ActionDescriptor desc);    
    
    public ActionBase getAction(ActionDescriptor desc);
}
