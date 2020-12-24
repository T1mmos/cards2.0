package gent.timdemey.cards.services.panels.load;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.panels.PanelBase;
import gent.timdemey.cards.services.panels.PanelManagerBase;
import net.miginfocom.swing.MigLayout;

public class LoadPanelManager extends PanelManagerBase
{
    private JLayeredPane loadPanel;
    
    @Override
    public boolean isCreated()
    {
        return loadPanel != null;
    }

    @Override
    public JLayeredPane create()
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
    public JLayeredPane get()
    {
        return loadPanel;
    }

    @Override
    public void preload()
    {
    }
}
