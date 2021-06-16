package gent.timdemey.cards.ui.components.ext;

public class PayloadBase implements IPayload
{
    private Object payload = null;
    
    @Override
    public Object getPayload()
    {
        return payload;
    }
    
    @Override
    public final void setPayload(Object payload)
    {
        this.payload = payload;
    }

}
