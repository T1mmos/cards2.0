package gent.timdemey.cards.ui.panels.menu;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.services.action.ActionBase;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptor;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptors;
import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptors;
import gent.timdemey.cards.services.contract.res.ImageResource;
import gent.timdemey.cards.services.interfaces.IActionService;
import gent.timdemey.cards.services.interfaces.IResourceCacheService;
import gent.timdemey.cards.services.interfaces.IResourceNameService;
import gent.timdemey.cards.ui.components.swing.JSButton;
import gent.timdemey.cards.ui.components.swing.JSLayeredPane;
import gent.timdemey.cards.ui.panels.PanelManagerBase;
import javax.swing.SwingConstants;
import net.miginfocom.swing.MigLayout;

public class MenuPanelManager extends PanelManagerBase
{
    private JSLayeredPane menuPanel;
    
    private BufferedImage bgimg;
    private final IActionService _ActionService;
    private final IResourceNameService _ResourceNameService;
    private final IResourceCacheService _ResourceCacheService;
    private final ICardPlugin _CardPlugin;

    public MenuPanelManager (Container container)
    {
        super(container);
        this._ActionService = container.Get(IActionService.class);
        this._ResourceNameService = container.Get(IResourceNameService.class);
        this._ResourceCacheService = container.Get(IResourceCacheService.class);
        this._CardPlugin = container.Get(ICardPlugin.class);
    }
    
    @Override
    public void destroyPanel()
    {
        menuPanel = null;
    }

    @Override
    public boolean isPanelCreated()
    {
        return menuPanel != null;
    }

    @Override
    public JSLayeredPane createPanel()
    {
        menuPanel = _JSFactory.createLayeredPane(ComponentTypes.PANEL_MENU);
        menuPanel.setLayout(new MigLayout("insets 0, align 50% 50%"));
        
        // icon
        {
            JLabel lbl_icon = _JSFactory.createLabel(new ImageIcon(bgimg));
            menuPanel.add(lbl_icon, "");
        }

        // buttons
        {
            JSLayeredPane pnl_buts = _JSFactory.createLayeredPane(ComponentTypes.PANEL);
            List<ActionDescriptor> actDescs = getMenuActionDescriptors();
            MenuButtonMouseListener listener = new MenuButtonMouseListener();          
            
            for (ActionDescriptor actDesc : actDescs)
            {
                ActionBase action = _ActionService.getAction(actDesc);
                JSButton button = _JSFactory.createButton(action);
                
                button.setContentAreaFilled(false);
                button.setFocusPainted(false);
                button.setHorizontalAlignment(SwingConstants.LEFT);
                button.setBorder(null);
                button.setBorderPainted(false);
                button.addMouseListener(listener);
                button.setIcon(null); // no icon in menu list

                pnl_buts.add(button, "sg buts, wrap");                
            }
            
            menuPanel.add(pnl_buts, "wrap");
        }
        
        return menuPanel;
    }
    
    @Override
    public JSLayeredPane getPanel()
    {
        return menuPanel;
    }
    
    protected List<ActionDescriptor> getMenuActionDescriptors()
    {
        List<ActionDescriptor> actdescs = new ArrayList<>();
        
        if(_CardPlugin.getPlayerCount() > 1)
        {
            actdescs.add(ActionDescriptors.CREATEMP);
            actdescs.add(ActionDescriptors.JOIN);
        }
        else
        {
            actdescs.add(ActionDescriptors.STARTSP);
        }
        
        actdescs.add(ActionDescriptors.SHOWSETTINGS);
        actdescs.add(ActionDescriptors.SHOWABOUT);
        actdescs.add(ActionDescriptors.QUIT);
        
        return actdescs;
    }

    @Override
    public void preload()
    {
        String iconFilename = _ResourceNameService.getFilePath(ResourceDescriptors.Menu);
        ImageResource res = _ResourceCacheService.getImage(iconFilename);
        bgimg = res.raw;
    }
 }
