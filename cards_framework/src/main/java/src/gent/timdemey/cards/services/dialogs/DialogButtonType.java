package gent.timdemey.cards.services.dialogs;

import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;

public enum DialogButtonType
{
    Ok(LocKey.Button_ok), 
    Cancel(LocKey.Button_cancel), 
    Yes(LocKey.Button_yes), 
    No(LocKey.Button_no), 
    Forced(LocKey.Button_cancel);

    final String loctext;

    private DialogButtonType(LocKey lockey)
    {
        this.loctext = Loc.get(lockey);
    }
}
