package gent.timdemey.cards.ui.panels.game;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.action.ActionBase;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptor;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptors;
import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.contract.descriptors.PanelButtonDescriptor;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptors;
import gent.timdemey.cards.services.interfaces.IActionService;
import gent.timdemey.cards.services.interfaces.IResourceCacheService;
import gent.timdemey.cards.services.interfaces.IResourceNameService;
import gent.timdemey.cards.ui.components.swing.JSButton;
import gent.timdemey.cards.ui.components.swing.JSFactory;
import gent.timdemey.cards.ui.components.swing.JSLayeredPane;
import gent.timdemey.cards.ui.panels.DataPanelManagerBase;
import gent.timdemey.cards.ui.panels.menu.MenuButtonMouseListener;

public class GameMenuPanelManager extends DataPanelManagerBase<Void, Void>
{
    private JSLayeredPane content;
    private BufferedImage bgimg;

    @Override
    public void preload()
    {
        IResourceNameService resLocServ = Services.get(IResourceNameService.class);
        String iconFilename = resLocServ.getFilePath(ResourceDescriptors.Menu);
        IResourceCacheService resCacheServ = Services.get(IResourceCacheService.class);
        bgimg = resCacheServ.getImage(iconFilename).raw;
    }

    @Override
    public JSLayeredPane createPanel()
    {
        if (content == null)
        {
            content = JSFactory.createLayeredPane(ComponentTypes.PANEL);
            if (bgimg == null)
            {
                preload();
            }
            content.add(JSFactory.createLabel(new ImageIcon(bgimg)), "");
            JSLayeredPane pnl_buts = JSFactory.createLayeredPane(ComponentTypes.PANEL);

            IActionService actServ = Services.get(IActionService.class);
            MenuButtonMouseListener listener = new MenuButtonMouseListener();
            for (ActionDescriptor actDesc : getMenuActionDescriptors())
            {
                ActionBase action = actServ.getAction(actDesc);
                JSButton button = JSFactory.createButton(action);

                button.setContentAreaFilled(false);
                button.setFocusPainted(false);
                button.setHorizontalAlignment(SwingConstants.LEFT);
                button.setBorder(null);
                button.setBorderPainted(false);
                button.addMouseListener(listener);
                button.setIcon(null);

                pnl_buts.add(button, "sg buts, wrap");
            }
            content.add(pnl_buts, "wrap");
        }

        return content;
    }

    protected List<ActionDescriptor> getMenuActionDescriptors()
    {
        List<ActionDescriptor> actdescs = new ArrayList<>();
        actdescs.add(ActionDescriptors.TOGGLEMENUMP);
        actdescs.add(ActionDescriptors.LEAVEMP);
        actdescs.add(ActionDescriptors.QUIT);
        return actdescs;
    }

    @Override
    public JSLayeredPane getPanel()
    {
        return content;
    }

    @Override
    public void destroyPanel()
    {
        content = null;
    }

    @Override
    public List<PanelButtonDescriptor> getButtonTypes()
    {
        return null;
    }

    @Override
    public Void onClose(PanelButtonDescriptor dbType)
    {
        return null;
    }

    @Override
    public boolean isButtonEnabled(PanelButtonDescriptor dbType)
    {
        return false;
    }

    @Override
    public String createTitle()
    {
        return "MENU";
    }
}
