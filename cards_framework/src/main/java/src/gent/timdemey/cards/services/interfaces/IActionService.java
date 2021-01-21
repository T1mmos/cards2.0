package gent.timdemey.cards.services.interfaces;

import java.util.function.Supplier;

import gent.timdemey.cards.services.action.ActionBase;
import gent.timdemey.cards.services.action.PayloadActionBase;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptor;
import gent.timdemey.cards.services.contract.descriptors.PayloadActionDescriptor;

public interface IActionService
{
    public ActionBase getAction(ActionDescriptor desc);
    public boolean canExecuteAction(ActionDescriptor desc);
    public void executeAction(ActionDescriptor desc);    
    
    public <T> PayloadActionBase<T> getAction(PayloadActionDescriptor<T> desc, Supplier<T> payloadSupplier);
    public <T> boolean canExecuteAction(PayloadActionDescriptor<T> desc, Supplier<T> payloadSupplier);
    public <T> void executeAction(PayloadActionDescriptor<T> desc, Supplier<T> payloadSupplier);
}
