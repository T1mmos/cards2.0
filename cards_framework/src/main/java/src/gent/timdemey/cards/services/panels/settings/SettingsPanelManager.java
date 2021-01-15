package gent.timdemey.cards.services.panels.settings;

import javax.swing.JButton;
import javax.swing.JLabel;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.action.ActionBase;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptors;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IActionService;
import gent.timdemey.cards.services.panels.PanelBase;
import gent.timdemey.cards.services.panels.PanelManagerBase;
import net.miginfocom.swing.MigLayout;

public class SettingsPanelManager extends PanelManagerBase
{
    private PanelBase contentPanel;
    
    @Override
    public void preload()
    {
        
    }

    @Override
    public PanelBase createPanel()
    {
        if (contentPanel == null)
        {
            contentPanel = new PanelBase(PanelDescriptors.SETTINGS, new MigLayout());
            
            contentPanel.add(new JLabel("Test"), "wrap");

            IActionService actServ = Services.get(IActionService.class);
            ActionBase act_tomenu = actServ.getAction(ActionDescriptors.SHOWMENU);
            contentPanel.add(new JButton(act_tomenu), "wrap");
        }
        
        return contentPanel;
    }

    @Override
    public PanelBase getPanel()
    {
        return contentPanel;
    }

    @Override
    public void destroyPanel()
    {
        contentPanel = null;
    }

}
