package gent.timdemey.cards.ui.panels.load;

import javax.swing.JLabel;

import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.ui.components.swing.JSFactory;
import gent.timdemey.cards.ui.components.swing.JSLayeredPane;
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
    public JSLayeredPane createPanel()
    {
        loadPanel = JSFactory.createLayeredPane(ComponentTypes.PANEL);
        loadPanel.setLayout(new MigLayout("insets 100, align 50% 50%"));
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
    public JSLayeredPane getPanel()
    {
        return loadPanel;
    }

    @Override
    public void preload()
    {
    }
}
