package gent.timdemey.cards.ui.panels.load;

import javax.swing.JLabel;

import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.ui.components.JSLayeredPane;
import gent.timdemey.cards.ui.panels.PanelManagerBase;
import net.miginfocom.swing.MigLayout;

public class LoadPanelManager extends PanelManagerBase
{
    private JSLayeredPane loadPanel;
    
    @Override
    public boolean isPanelCreated()
    {
        return loadPanel != null;
    }

    @Override
    public JSLayeredPane createSPanel()
    {
        loadPanel = new JSLayeredPane(new MigLayout("insets 100, align 50% 50%"), PanelDescriptors.Load.id);
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
    public JSLayeredPane getSPanel()
    {
        return loadPanel;
    }

    @Override
    public void preload()
    {
    }
}
