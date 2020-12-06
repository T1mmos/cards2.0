package gent.timdemey.cards.services.interfaces;

import gent.timdemey.cards.services.dialogs.PanelCreatorBase;
import gent.timdemey.cards.services.dialogs.DialogOutData;

public interface IDialogService
{
    public void ShowNotImplemented();

    public DialogOutData<Void> ShowMessage(String title, String message);

    public DialogOutData<Void> ShowInternalError();

    public <IN, OUT> DialogOutData<OUT> ShowAdvanced(String title, IN data, PanelCreatorBase<IN, OUT> dialogContent);
}
