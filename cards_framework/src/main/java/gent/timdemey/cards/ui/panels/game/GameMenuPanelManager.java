package gent.timdemey.cards.ui.panels.game;

import gent.timdemey.cards.di.Container;
import java.util.List;

import javax.swing.JButton;

import gent.timdemey.cards.services.action.ActionBase;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptors;
import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.contract.descriptors.PanelButtonDescriptor;
import gent.timdemey.cards.ui.components.swing.JSLayeredPane;
import gent.timdemey.cards.ui.panels.DataPanelManagerBase;

public class GameMenuPanelManager extends DataPanelManagerBase<Void, Void>
{
    private JSLayeredPane content;

    public GameMenuPanelManager (Container container)
    {
        super(container);
    }
    
    @Override
    public void preload()
    {

    }

    @Override
    public JSLayeredPane createPanel()
    {
        if (content == null)
        {
            content = _JSFactory.createLayeredPane(ComponentTypes.PANEL);
            
            JButton btn_return = _JSFactory.createButton("Return to game (dummy)");
            
            ActionBase act_leavemp = _ActionService.getAction(ActionDescriptors.LEAVEMP, ActionDescriptors.TOGGLEMENUMP);
            JButton btn_leavemp = _JSFactory.createButton(act_leavemp);
            
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
