package gent.timdemey.cards.services.interfaces;

import gent.timdemey.cards.services.contract.descriptors.DataPanelDescriptor;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptor;
import gent.timdemey.cards.services.contract.preload.IPreload;
import gent.timdemey.cards.services.panels.IDataPanelManager;
import gent.timdemey.cards.services.panels.IPanelManager;

public interface IPanelService extends IPreload
{
    public PanelDescriptor getDefaultPanelDescriptor();
    public IPanelManager getPanelManager(PanelDescriptor panelDesc);
    public <IN, OUT> IDataPanelManager<IN, OUT> getPanelManager(DataPanelDescriptor<IN, OUT> panelDesc);
    
    public void relayout();
    public void rescaleResourcesAsync();


}
