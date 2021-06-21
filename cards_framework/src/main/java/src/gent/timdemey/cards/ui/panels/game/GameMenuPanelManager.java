package gent.timdemey.cards.ui.panels.game;

import java.util.List;

import javax.swing.JButton;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptors;
import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.contract.descriptors.PanelButtonDescriptor;
import gent.timdemey.cards.services.interfaces.IActionService;
import gent.timdemey.cards.ui.components.swing.JSFactory;
import gent.timdemey.cards.ui.components.swing.JSLayeredPane;
import gent.timdemey.cards.ui.panels.DataPanelManagerBase;

public class GameMenuPanelManager extends DataPanelManagerBase<Void, Void>
{
    private JSLayeredPane content;

    @Override
    public void preload()
    {

    }

    @Override
    public JSLayeredPane createPanel()
    {
        if (content == null)
        {
            content = JSFactory.createLayeredPane(ComponentTypes.PANEL);
            content.setOpaque(false);
            
            IActionService actServ = Services.get(IActionService.class);            

            JButton btn_return = JSFactory.createButton("Return to game (dummy)");
            JButton btn_leavemp = JSFactory.createButton(actServ.getAction(ActionDescriptors.LEAVEMP));
            
            content.add(btn_return, "");
            content.add(btn_leavemp, "wrap");            
        }
        
        return content;
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
        return "GAME MENU (DUMMY)";
    }
}
