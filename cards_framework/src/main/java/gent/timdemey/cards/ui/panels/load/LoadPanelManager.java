package gent.timdemey.cards.ui.panels.load;

import gent.timdemey.cards.di.Container;
import java.awt.Color;
import java.awt.Font;

import javax.swing.border.EmptyBorder;

import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.ui.components.swing.JSLabel;
import gent.timdemey.cards.ui.components.swing.JSLayeredPane;
import gent.timdemey.cards.ui.panels.PanelManagerBase;
import net.miginfocom.swing.MigLayout;

public class LoadPanelManager extends PanelManagerBase
{
    private JSLayeredPane loadPanel;
    
    public LoadPanelManager (Container container)
    {
        super(container);
    }
    
    @Override
    public boolean isPanelCreated()
    {
        return loadPanel != null;
    }

    @Override
    public JSLayeredPane createPanel()
    {
        loadPanel = _JSFactory.createLayeredPane(ComponentTypes.PANEL);
        loadPanel.setLayout(new MigLayout("insets 10, align 100% 100%"));
        loadPanel.setBackground(Color.decode("#1E3F5A"));
        
        JSLabel label = _JSFactory.createLabel("LOADING...");
        label.setBackground(Color.DARK_GRAY.darker().darker());
        
        label.setFont(Font.decode("Verdana 24"));
        label.setForeground(Color.gray);
        label.setBorder(new EmptyBorder(5,5,5,5));
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
