package gent.timdemey.cards.services.contract.descriptors;

import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.services.action.ActionBase;

public class PanelButtonDescriptor
{
    public final LocKey lockey;
    public final ActionBase action;
    
    public PanelButtonDescriptor(LocKey lockey)
    {
        if (lockey == null)
        {
            throw new NullPointerException("lockey");
        }
        this.lockey = lockey;
        this.action = null;
    }
    
    public PanelButtonDescriptor(ActionBase action)
    {
        if (action == null)
        {
            throw new NullPointerException("action");
        }
        
        this.lockey = null;
        this.action = action;
    }
}
