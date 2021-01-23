package gent.timdemey.cards.services.panels.about;

import javax.swing.JButton;
import javax.swing.JLabel;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.common.Version;
import gent.timdemey.cards.services.action.ActionBase;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptors;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IActionService;
import gent.timdemey.cards.services.panels.PanelManagerBase;
import gent.timdemey.cards.ui.PanelBase;
import net.miginfocom.swing.MigLayout;

public class AboutPanelManager extends PanelManagerBase
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
            contentPanel = new PanelBase(new MigLayout(), PanelDescriptors.About.id);
            
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
