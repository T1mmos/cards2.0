package gent.timdemey.cards.services.interfaces;

import java.util.List;

import gent.timdemey.cards.services.contract.descriptors.DataPanelDescriptor;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptor;
import gent.timdemey.cards.services.contract.preload.IPreload;
import gent.timdemey.cards.ui.panels.IDataPanelManager;
import gent.timdemey.cards.ui.panels.IPanelManager;

public interface IPanelService extends IPreload
{
    public PanelDescriptor getDefaultPanelDescriptor();
    public List<PanelDescriptor> getPanelDescriptors();
    
    public IPanelManager getPanelManager(PanelDescriptor panelDesc);
    public <IN, OUT> IDataPanelManager<IN, OUT> getPanelManager(DataPanelDescriptor<IN, OUT> panelDesc);    
    
    public void positionComponents();
    
    public void rescaleResourcesAsync(Runnable callback);
    public void createComponentsAsync(Runnable callback);
}
