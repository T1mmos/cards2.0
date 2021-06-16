package gent.timdemey.cards.ui.panels.about;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.common.Version;
import gent.timdemey.cards.services.action.ActionBase;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptors;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IActionService;
import gent.timdemey.cards.ui.components.JSLayeredPane;
import gent.timdemey.cards.ui.panels.PanelManagerBase;
import net.miginfocom.swing.MigLayout;

public class AboutPanelManager extends PanelManagerBase
{
    private JSLayeredPane contentPanel;
    
    @Override
    public void preload()
    {
        
    }

    @Override
    public JSLayeredPane createSPanel()
    {
        if (contentPanel == null)
        {
            contentPanel = new JSLayeredPane(PanelDescriptors.About, new MigLayout());
            
        
            ICardPlugin plugin = Services.get(ICardPlugin.class);
            IActionService actServ = Services.get(IActionService.class);
            
            ActionBase act_tomenu = actServ.getAction(ActionDescriptors.SHOWMENU);
            
            String gamename = plugin.getName();
            Version version = plugin.getVersion();
            String versionstr = "v"+version.getMajor()+"."+version.getMinor();
            
            
            contentPanel.add(new JLabel(gamename), "wrap");
            contentPanel.add(new JLabel(versionstr), "wrap");
            contentPanel.add(new JButton(act_tomenu), "wrap");
        }
        
        return contentPanel;
    }

    @Override
    public JSLayeredPane getSPanel()
    {
        return contentPanel;
    }

    @Override
    public void destroyPanel()
    {
        contentPanel = null;
    }

}
