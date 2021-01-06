package gent.timdemey.cards.services.interfaces;

import gent.timdemey.cards.services.action.ActionBase;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptor;

public interface IActionService
{
    public boolean canExecuteAction(ActionDescriptor desc);

    public void executeAction(ActionDescriptor desc);    
    
    public ActionBase getAction(ActionDescriptor desc);
}
