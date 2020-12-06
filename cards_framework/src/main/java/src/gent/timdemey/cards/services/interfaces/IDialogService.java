package gent.timdemey.cards.services.interfaces;

import gent.timdemey.cards.services.dialogs.DialogContentCreator;
import gent.timdemey.cards.services.dialogs.DialogOutData;

public interface IDialogService
{
    public void ShowNotImplemented();

    public DialogOutData<Void> ShowMessage(String title, String message);

    public DialogOutData<Void> ShowInternalError();

    public <IN, OUT> DialogOutData<OUT> ShowAdvanced(String title, IN data, DialogContentCreator<IN, OUT> dialogContent);
}
