package gent.timdemey.cards.ui.panels;

import java.util.List;

import gent.timdemey.cards.services.contract.descriptors.PanelButtonDescriptor;

public interface IDataPanelManager<IN, OUT> extends IPanelManager
{
    public List<PanelButtonDescriptor> getButtonTypes();
    public void load(PanelInData<IN> inData);
    public OUT onClose(PanelButtonDescriptor dbType);
    public boolean isButtonEnabled(PanelButtonDescriptor dbType);
    
    public String createTitle();
}
