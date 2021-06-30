package gent.timdemey.cards.ui.panels.load;

import java.awt.Color;
import java.awt.Font;

import javax.swing.border.EmptyBorder;

import com.alee.utils.ColorUtils;

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
        loadPanel.setLayout(new MigLayout("insets 10, align 100% 100%"));
        loadPanel.setBackground(ColorUtils.fromHex("1E3F5A"));
        
        JSLabel label = JSFactory.createLabel("LOADING...");
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
