package gent.timdemey.cards.services.interfaces;

import gent.timdemey.cards.ui.panels.IDataPanelManager;
import gent.timdemey.cards.ui.panels.PanelOutData;

public interface IDialogService
{
    public void ShowNotImplemented();

    public PanelOutData<Void> ShowMessage(String title, String message);

    public PanelOutData<Void> ShowInternalError();

    public <IN, OUT> PanelOutData<OUT> ShowAdvanced(String title, IN data, IDataPanelManager<IN, OUT> dialogContent);
}
