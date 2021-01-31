package gent.timdemey.cards.services.contract.descriptors;

import gent.timdemey.cards.localization.LocKey;

public class PanelButtonDescriptors
{
    public static PanelButtonDescriptor Ok = create(LocKey.Button_ok);
    public static PanelButtonDescriptor Cancel= create(LocKey.Button_cancel);
    public static PanelButtonDescriptor Yes= create(LocKey.Button_yes);
    public static PanelButtonDescriptor No= create(LocKey.Button_no);
    public static PanelButtonDescriptor Forced= create(LocKey.Button_cancel);
    
    public static PanelButtonDescriptor create (LocKey lockey)
    {
        return new PanelButtonDescriptor(lockey);
    }
    
    private PanelButtonDescriptors()
    {
    }
}
