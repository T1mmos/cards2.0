package gent.timdemey.cards.ui.panels.about;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.common.Version;
import gent.timdemey.cards.di.Container;
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
    private final ICardPlugin _CardPlugin;
    private final IActionService _ActionService;
    
    public AboutPanelManager(Container container)
    {
        super(container);
        this._CardPlugin = container.Get(ICardPlugin.class);
        this._ActionService = container.Get(IActionService.class);
    }
    
    @Override
    public void preload()
    {
        
    }

    @Override
    public JSLayeredPane createPanel()
    {
        if (contentPanel == null)
        {
            contentPanel = _JSFactory.createLayeredPane(ComponentTypes.PANEL);
                    
            ActionBase act_tomenu = _ActionService.getAction(ActionDescriptors.SHOWMENU);
            
            String gamename = _CardPlugin.getName();
            Version version = _CardPlugin.getVersion();
            String versionstr = "v"+version.getMajor()+"."+version.getMinor();
            
            contentPanel.add(_JSFactory.createLabel(gamename), "wrap");
            contentPanel.add(_JSFactory.createLabel(versionstr), "wrap");
            contentPanel.add(_JSFactory.createButton(act_tomenu), "wrap");
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
