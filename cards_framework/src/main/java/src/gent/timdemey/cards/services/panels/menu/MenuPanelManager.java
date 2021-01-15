package gent.timdemey.cards.services.panels.menu;

import java.util.ArrayList;
import java.util.List;

import com.alee.laf.button.WebButton;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.action.ActionBase;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptor;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptors;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IActionService;
import gent.timdemey.cards.services.panels.PanelBase;
import gent.timdemey.cards.services.panels.PanelManagerBase;
import net.miginfocom.swing.MigLayout;

public class MenuPanelManager extends PanelManagerBase
{
    private PanelBase menuPanel;

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
    public PanelBase createPanel()
    {
        menuPanel = new PanelBase(PanelDescriptors.MENU);
        menuPanel.setLayout(new MigLayout("insets 0, align 50% 50%"));

        List<ActionDescriptor> actDescs = getMenuActionDescriptors();

        MenuButtonMouseListener listener = new MenuButtonMouseListener();
            
        IActionService actServ = Services.get(IActionService.class);
        for (ActionDescriptor actDesc : actDescs)
        {
            ActionBase action = actServ.getAction(actDesc);
            WebButton button = new WebButton(action);
            
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setBorder(null);
            button.setBorderPainted(false);
            button.setOpaque(false);
            button.addMouseListener(listener);
            button.setIcon(null); // no icon in menu list

            menuPanel.add(button, "sg buts, wrap");
            
            menuPanel.setOpaque(false);
        }
        
        return menuPanel;
    }
    
    @Override
    public PanelBase getPanel()
    {
        return menuPanel;
    }
    
    protected List<ActionDescriptor> getMenuActionDescriptors()
    {
        ICardPlugin cardPlugin = Services.get(ICardPlugin.class);
        List<ActionDescriptor> actdescs = new ArrayList<>();
        
        if(cardPlugin.getPlayerCount() > 1)
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
        
    }
 }
