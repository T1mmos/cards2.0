package gent.timdemey.cards.ui.panels.load;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;

import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.ui.components.swing.JSFactory;
import gent.timdemey.cards.ui.components.swing.JSLabel;
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
        
        JSLabel label = JSFactory.createLabel("LOADING...");
        label.setFont(Font.decode("Arial 60 bold"));
        label.setForeground(Color.black);
        loadPanel.add(label);
        
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
