package gent.timdemey.cards.services.action;

import java.awt.event.ActionEvent;
import java.util.function.Supplier;


import gent.timdemey.cards.services.contract.descriptors.PayloadActionDescriptor;
import gent.timdemey.cards.services.interfaces.IActionService;

public class PayloadActionBase<T> extends ActionBase
{
    private final Supplier<T> payloadSupplier;

    PayloadActionBase(PayloadActionDescriptor<T> desc, String title, Supplier<T> payloadSupplier)
    {
        super(desc, title);
        
        if (payloadSupplier == null)
        {
            throw new NullPointerException("payloadSupplier");
        }
        
        this.payloadSupplier = payloadSupplier;
    }
    
    @Override
    public final void actionPerformed(ActionEvent e)
    {
        @SuppressWarnings("unchecked")
        PayloadActionDescriptor<T> tdesc = (PayloadActionDescriptor<T>) desc;
        T payload = payloadSupplier.get();
        Services.get(IActionService.class).executeAction(tdesc, payload);
    }
    
    @Override
    public String toString()
    {
        return "ActionBase { " + desc + "}";
    }
    
    @Override
    public void checkEnabled()
    {
        IActionService actServ = Services.get(IActionService.class);
        @SuppressWarnings("unchecked")
        PayloadActionDescriptor<T> tdesc = (PayloadActionDescriptor<T>) desc;
        T payload = payloadSupplier.get();
        boolean canExecute = actServ.canExecuteAction(tdesc, payload);
        setEnabled(canExecute);
    }
}

