package gent.timdemey.cards.services.panels;

import java.awt.Font;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IResourceLocationService;
import gent.timdemey.cards.services.interfaces.IResourceService;
import gent.timdemey.cards.ui.actions.ActionDescriptor;
import gent.timdemey.cards.ui.actions.ActionDescriptors;
import gent.timdemey.cards.ui.actions.IActionService;
import net.miginfocom.swing.MigLayout;

public class MenuPanelManager extends DataPanelManagerBase<Void, Void>
{

    private PanelBase menuPanel;
    
    @Override
    public EnumSet<PanelButtonType> getButtonTypes()
    {
        // there are no standard buttons
        return null;
    }

    @Override
    public void onShow()
    {
        
    }

    @Override
    public Void onClose(PanelButtonType dbType)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isButtonEnabled(PanelButtonType dbType)
    {
        // there are no standard buttons
        return false;
    }

    @Override
    public JComponent create()
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
            JButton button = new JButton(actServ.getAction(actDesc));
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setBorder(null);
            button.setBorderPainted(false);
            button.setOpaque(false);

            button.addMouseListener(listener);
            button.setFont(font);
            button.setIcon(null); // no icon in menu list

            menuPanel.add(button, "sg buts, wrap");
        }
        
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
        // TODO Auto-generated method stub
        
    }

    @Override
    public void destroy()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void createRescaleRequests(List<? super RescaleRequest> requests)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void createScalableComponents()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void positionScalableComponents()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onResourcesRescaled()
    {
        // TODO Auto-generated method stub
        
    }

}
