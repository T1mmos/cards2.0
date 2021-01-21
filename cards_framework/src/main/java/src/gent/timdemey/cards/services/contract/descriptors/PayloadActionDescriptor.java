package gent.timdemey.cards.services.contract.descriptors;

public class PayloadActionDescriptor<T> extends ActionDescriptor
{
    PayloadActionDescriptor (String id)
    {
        super(id);
    }
    
    @Override
    public String toString()
    {
        return String.format("PayloadActionDescriptor { id=%s }", id);  
    }
}
