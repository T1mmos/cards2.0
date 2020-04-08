package gent.timdemey.cards.services.dialogs;

import java.util.EnumSet;

import gent.timdemey.cards.localization.Loc;

public enum DialogButtonType
{
    Ok("button_ok"), Cancel("button_cancel"), Yes("button_yes"), No("button_no"), Forced("button_cancel");

    public static final EnumSet<DialogButtonType> BUTTONS_OK_CANCEL = EnumSet.of(DialogButtonType.Ok,
            DialogButtonType.Cancel);
    public static final EnumSet<DialogButtonType> BUTTONS_YES_NO = EnumSet.of(DialogButtonType.Yes,
            DialogButtonType.No);

    final String loctext;

    private DialogButtonType(String lockey)
    {
        this.loctext = Loc.get(lockey);
    }
}
