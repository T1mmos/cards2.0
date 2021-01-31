package gent.timdemey.cards.services.panels.game;

import java.util.List;

import javax.swing.JButton;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptors;
import gent.timdemey.cards.services.contract.descriptors.PanelButtonDescriptor;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IActionService;
import gent.timdemey.cards.services.panels.DataPanelManagerBase;
import gent.timdemey.cards.ui.PanelBase;
import net.miginfocom.swing.MigLayout;

public class GameMenuPanelManager extends DataPanelManagerBase<Void, Void>
{
    private PanelBase content;

    @Override
    public void preload()
    {

    }

    @Override
    public PanelBase createPanel()
    {
        if (content == null)
        {
            content = new PanelBase(new MigLayout(), PanelDescriptors.GameMenu.id);
            
            IActionService actServ = Services.get(IActionService.class);            

            JButton btn_return = new JButton("Return to game (dummy)");
            JButton btn_leavemp = new JButton(actServ.getAction(ActionDescriptors.LEAVEMP));
            
            content.add(btn_return, "");
            content.add(btn_leavemp, "wrap");            
        }
        
        return content;
    }

    @Override
    public PanelBase getPanel()
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
        return "GAME MENU (DUMMY)";
    }
}
