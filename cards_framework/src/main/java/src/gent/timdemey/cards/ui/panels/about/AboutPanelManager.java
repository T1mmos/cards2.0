package gent.timdemey.cards.ui.panels.about;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.common.Version;
import gent.timdemey.cards.services.action.ActionBase;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptors;
import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.interfaces.IActionService;
import gent.timdemey.cards.ui.components.swing.JSFactory;
import gent.timdemey.cards.ui.components.swing.JSLayeredPane;
import gent.timdemey.cards.ui.panels.PanelManagerBase;

public class AboutPanelManager extends PanelManagerBase
{
    private JSLayeredPane contentPanel;
    
    @Override
    public void preload()
    {
        
    }

    @Override
    public JSLayeredPane createPanel()
    {
        if (contentPanel == null)
        {
            contentPanel = JSFactory.createLayeredPane(ComponentTypes.PANEL);
        
            ICardPlugin plugin = Services.get(ICardPlugin.class);
            IActionService actServ = Services.get(IActionService.class);
            
            ActionBase act_tomenu = actServ.getAction(ActionDescriptors.SHOWMENU);
            
            String gamename = plugin.getName();
            Version version = plugin.getVersion();
            String versionstr = "v"+version.getMajor()+"."+version.getMinor();
            
            contentPanel.add(JSFactory.createLabel(gamename), "wrap");
            contentPanel.add(JSFactory.createLabel(versionstr), "wrap");
            contentPanel.add(JSFactory.createButton(act_tomenu), "wrap");
        }
        
        return contentPanel;
    }

    @Override
    public JSLayeredPane getPanel()
    {
        return contentPanel;
    }

    @Override
    public void destroyPanel()
    {
        contentPanel = null;
    }
}
