package gent.timdemey.cards.services.panels;

import javax.swing.JComponent;
import javax.swing.JLabel;

import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import net.miginfocom.swing.MigLayout;

public class LoadPanelManager extends PanelManagerBase
{
    private PanelBase loadPanel;
    
    @Override
    public boolean isCreated()
    {
        return loadPanel != null;
    }

    @Override
    public JComponent create()
    {
        loadPanel = new PanelBase(PanelDescriptors.LOAD);
        loadPanel.setOpaque(false); 
        loadPanel.setLayout(new MigLayout("insets 0, align 50% 50%"));
        loadPanel.add(new JLabel("LOADING..."));
        
        return loadPanel;
    }
    
    @Override
    public void destroy()
    {
        loadPanel = null;
    }

    @Override
    public JComponent get()
    {
        return loadPanel;
    }
}
