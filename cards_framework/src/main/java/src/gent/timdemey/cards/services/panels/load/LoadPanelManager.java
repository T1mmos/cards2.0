package gent.timdemey.cards.services.panels.load;

import javax.swing.JLabel;

import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.panels.PanelBase;
import gent.timdemey.cards.services.panels.PanelManagerBase;
import net.miginfocom.swing.MigLayout;

public class LoadPanelManager extends PanelManagerBase
{
    private PanelBase loadPanel;
    
    @Override
    public boolean isPanelCreated()
    {
        return loadPanel != null;
    }

    @Override
    public PanelBase createPanel()
    {
        loadPanel = new PanelBase(PanelDescriptors.LOAD, new MigLayout("insets 100, align 50% 50%"));
        loadPanel.setOpaque(false); 
        loadPanel.add(new JLabel("LOADING..."));
        
        return loadPanel;
    }
    
    @Override
    public void destroyPanel()
    {
        loadPanel = null;
    }

    @Override
    public PanelBase getPanel()
    {
        return loadPanel;
    }

    @Override
    public void preload()
    {
    }
}
