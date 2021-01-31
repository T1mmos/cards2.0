package gent.timdemey.cards.services.contract.descriptors;

import javax.swing.Action;

import gent.timdemey.cards.localization.LocKey;

public class PanelButtonDescriptor
{
    public final LocKey lockey;
    public final Action action;
    
    public PanelButtonDescriptor(LocKey lockey)
    {
        if (lockey == null)
        {
            throw new NullPointerException("lockey");
        }
        this.lockey = lockey;
        this.action = null;
    }
    
    public PanelButtonDescriptor(Action action)
    {
        if (action == null)
        {
            throw new NullPointerException("action");
        }
        
        this.lockey = null;
        this.action = action;
    }
}
