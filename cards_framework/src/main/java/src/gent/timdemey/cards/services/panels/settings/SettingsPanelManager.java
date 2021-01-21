package gent.timdemey.cards.services.panels.settings;

import java.awt.Color;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.entities.commands.payload.P_SaveState;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.action.ActionBase;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptors;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IActionService;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.panels.PanelBase;
import gent.timdemey.cards.services.panels.PanelManagerBase;
import net.miginfocom.swing.MigLayout;

public class SettingsPanelManager extends PanelManagerBase
{
    private PanelBase contentPanel;
    private JTextField tf_pname;
        
    @Override
    public void preload()
    {
        
    }

    @Override
    public PanelBase createPanel()
    {
        if (contentPanel == null)
        {
            contentPanel = new PanelBase(PanelDescriptors.SETTINGS, new MigLayout());
            
            JPanel floatPanel = new JPanel(new MigLayout("inset 10"));
            floatPanel.setBackground(new Color(50,50,50,150));
            contentPanel.add(floatPanel, "hmin 200, wmin 350, push, alignx center, aligny center");
            
            IContextService ctxtServ = Services.get(IContextService.class);
            ReadOnlyState state = ctxtServ.getThreadContext().getReadOnlyState();
            
            // player name
            {
                JLabel lbl_pname = new JLabel(Loc.get(LocKey.Label_playername));
                floatPanel.add(lbl_pname, "");
                
                tf_pname = new JTextField(state.getLocalName(), 30);
                floatPanel.add(tf_pname, "gapright 100, wrap");
            }
            
            // buttons
            {
                IActionService actServ = Services.get(IActionService.class);
                
                floatPanel.add(new JSeparator(), "span, push, growx, aligny bottom, wrap");
                
                ActionBase act_tomenu = actServ.getAction(ActionDescriptors.SHOWMENU);
                floatPanel.add(new JButton(act_tomenu), "span, split 2, pushx, center, sg buts");
                
                ActionBase act_savecfg = actServ.getAction(ActionDescriptors.SAVECFG, this::getPayload);               
                floatPanel.add(new JButton(act_savecfg), "sg buts");
            }
        }
        
        return contentPanel;
    }
    
    private P_SaveState getPayload()
    {
        P_SaveState payload = new P_SaveState();
        payload.id = UUID.randomUUID();
        payload.playerName = tf_pname.getText();
        return payload;
    }    
    
    @Override
    public PanelBase getPanel()
    {
        return contentPanel;
    }

    @Override
    public void destroyPanel()
    {
        contentPanel = null;
    }

}
