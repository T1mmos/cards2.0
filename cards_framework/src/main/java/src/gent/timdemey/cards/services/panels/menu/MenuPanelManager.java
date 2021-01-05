package gent.timdemey.cards.services.panels.menu;

import java.awt.Font;
import java.util.Arrays;
import java.util.List;

import com.alee.laf.button.WebButton;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptor;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptors;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IResourceLocationService;
import gent.timdemey.cards.services.interfaces.IResourceService;
import gent.timdemey.cards.services.panels.PanelBase;
import gent.timdemey.cards.services.panels.PanelManagerBase;
import gent.timdemey.cards.ui.actions.IActionService;
import net.miginfocom.swing.MigLayout;

public class MenuPanelManager extends PanelManagerBase
{
    private PanelBase menuPanel;

    @Override
    public void destroy()
    {
        menuPanel = null;
    }

    @Override
    public boolean isCreated()
    {
        return menuPanel != null;
    }

    @Override
    public PanelBase create()
    {
        menuPanel = new PanelBase(PanelDescriptors.MENU);
        menuPanel.setLayout(new MigLayout("insets 0, align 50% 50%"));

        List<ActionDescriptor> actDescs = getMenuActionDescriptors();

        MenuButtonMouseListener listener = new MenuButtonMouseListener();
        IResourceLocationService resLocServ = Services.get(IResourceLocationService.class);
        IResourceService resServ = Services.get(IResourceService.class);
        
        Font font = resServ.getFont(resLocServ.getMenuFontFilePath()).raw.deriveFont(30f);

        IActionService actServ = Services.get(IActionService.class);
        for (ActionDescriptor actDesc : actDescs)
        {
            WebButton button = new WebButton(actServ.getAction(actDesc));
         //   button.setForeground(Color.black);
            
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setBorder(null);
            button.setBorderPainted(false);
            button.setOpaque(false);
            button.addMouseListener(listener);
      //     button.setFont(font);
            button.setIcon(null); // no icon in menu list

            menuPanel.add(button, "sg buts, wrap");
        }
        
        return menuPanel;
    }
    
    @Override
    public PanelBase get()
    {
        return menuPanel;
    }
    
    protected List<ActionDescriptor> getMenuActionDescriptors()
    {
        ICardPlugin cardPlugin = Services.get(ICardPlugin.class);
        if(cardPlugin.getPlayerCount() > 1)
        {
            return Arrays.asList(ActionDescriptors.ad_create_mp, ActionDescriptors.ad_join, ActionDescriptors.ad_quit);
        }
        else
        {
            return Arrays.asList(ActionDescriptors.ad_start, ActionDescriptors.ad_quit);
        }
    }

    @Override
    public void preload()
    {
        
    }
 }
