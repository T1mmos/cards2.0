package gent.timdemey.cards.services.panels;

import gent.timdemey.cards.localization.LocKey;

public enum PanelButtonType
{
    Ok(LocKey.Button_ok), 
    Cancel(LocKey.Button_cancel), 
    Yes(LocKey.Button_yes), 
    No(LocKey.Button_no), 
    Forced(LocKey.Button_cancel);

    final LocKey lockey;

    private PanelButtonType(LocKey lockey)
    {
        this.lockey = lockey;
    }
}
