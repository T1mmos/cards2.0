package gent.timdemey.cards.services.action;

import java.awt.event.ActionEvent;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.contract.descriptors.PayloadActionDescriptor;
import gent.timdemey.cards.services.interfaces.IActionService;

public class PayloadActionBase<T> extends ActionBase
{
    private final T payload;

    PayloadActionBase(PayloadActionDescriptor<T> desc, String title, T payload)
    {
        super(desc, title);
        
        if (payload == null)
        {
            throw new NullPointerException("payload");
        }
        
        this.payload = payload;
    }
    
    @Override
    public final void actionPerformed(ActionEvent e)
    {
        @SuppressWarnings("unchecked")
        PayloadActionDescriptor<T> tdesc = (PayloadActionDescriptor<T>) desc;
        Services.get(IActionService.class).executeAction(tdesc, payload);
    }
    
    @Override
    public String toString()
    {
        return "ActionBase { " + desc + "}";
    }
    
    @Override
    protected void checkEnabled()
    {
        @SuppressWarnings("unchecked")
        PayloadActionDescriptor<T> tdesc = (PayloadActionDescriptor<T>) desc;
        setEnabled(Services.get(IActionService.class).canExecuteAction(tdesc, payload));
    }
}

