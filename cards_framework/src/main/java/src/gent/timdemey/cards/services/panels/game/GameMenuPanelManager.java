package gent.timdemey.cards.services.panels.game;

import java.awt.event.KeyEvent;
import java.util.EnumSet;

import javax.swing.JButton;
import javax.swing.KeyStroke;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptors;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IActionService;
import gent.timdemey.cards.services.panels.DataPanelManagerBase;
import gent.timdemey.cards.services.panels.PanelBase;
import gent.timdemey.cards.services.panels.PanelButtonType;
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
            content = new PanelBase(PanelDescriptors.GAME_MENU, new MigLayout());
            
            IActionService actServ = Services.get(IActionService.class);            

            JButton btn_return = new JButton("Return to game (dummy)");
            JButton btn_leavemp = new JButton(actServ.getAction(ActionDescriptors.ad_leavemp));
            
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
    public EnumSet<PanelButtonType> getButtonTypes()
    {
        return EnumSet.noneOf(PanelButtonType.class);
    }

    @Override
    public Void onClose(PanelButtonType dbType)
    {
        return null;
    }

    @Override
    public boolean isButtonEnabled(PanelButtonType dbType)
    {
        return false;
    }

    @Override
    public String createTitle()
    {
        return "GAME MENU (DUMMY)";
    }
}
