package gent.timdemey.cards.services.interfaces;

import java.util.EnumSet;

import gent.timdemey.cards.services.dialogs.DialogButtonType;
import gent.timdemey.cards.services.dialogs.DialogContent;
import gent.timdemey.cards.services.dialogs.DialogData;

public interface IDialogService
{
    public void ShowNotImplemented();

    public DialogData<Void> ShowMessage(String title, String message);

    public DialogData<Void> ShowInternalError();

    public <IN, OUT> DialogData<OUT> ShowAdvanced(String title, IN data, DialogContent<IN, OUT> dialogContent,
            DialogButtonType closeType);

    public <IN, OUT> DialogData<OUT> ShowAdvanced(String title, IN data, DialogContent<IN, OUT> dialogContent,
            EnumSet<DialogButtonType> closeTypes);
}
