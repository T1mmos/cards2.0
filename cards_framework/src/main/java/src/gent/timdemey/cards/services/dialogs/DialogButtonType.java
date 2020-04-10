package gent.timdemey.cards.services.dialogs;

import java.util.EnumSet;

import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;

public enum DialogButtonType
{
    Ok(LocKey.Button_ok), 
    Cancel(LocKey.Button_cancel), 
    Yes(LocKey.Button_yes), 
    No(LocKey.Button_no), 
    Forced(LocKey.Button_cancel);

    public static final EnumSet<DialogButtonType> BUTTONS_OK_CANCEL = EnumSet.of(DialogButtonType.Ok,
            DialogButtonType.Cancel);
    public static final EnumSet<DialogButtonType> BUTTONS_YES_NO = EnumSet.of(DialogButtonType.Yes,
            DialogButtonType.No);

    final String loctext;

    private DialogButtonType(LocKey lockey)
    {
        this.loctext = Loc.get(lockey);
    }
}
