package gent.timdemey.cards.ui.panels.about;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.services.action.ActionBase;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptors;
import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.interfaces.IActionService;
import gent.timdemey.cards.services.interfaces.IResourceCacheService;
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
            String versionstr = getAppVersionText();
            
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

    /**
     * Reads app.version from app-version.properties: "DEV BUILD" in the source tree,
     * replaced by the release version (e.g. 1.0.1) in installer builds.
     */
    private static String getAppVersionText()
    {
        try
        {
            IResourceCacheService resCacheServ = Services.get(IResourceCacheService.class);
            String v = resCacheServ.getProperties("app-version.properties").raw.getProperty("app.version");
            if (v == null || v.isBlank())
            {
                throw new IllegalStateException("app.version not set in app-version.properties");
            }
            v = v.trim();
            return Character.isDigit(v.charAt(0)) ? "v" + v : v;
        }
        catch (RuntimeException e)
        {
            Logger.error(e);
            return "NO VERSION FOUND";
        }
    }
}
