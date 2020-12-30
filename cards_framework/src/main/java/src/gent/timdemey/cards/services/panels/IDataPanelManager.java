package gent.timdemey.cards.services.panels;

import java.util.EnumSet;

public interface IDataPanelManager<IN, OUT> extends IPanelManager
{
    public EnumSet<PanelButtonType> getButtonTypes();
    public void load(PanelInData<IN> inData);
    public OUT onClose(PanelButtonType dbType);
    public boolean isButtonEnabled(PanelButtonType dbType);
}
