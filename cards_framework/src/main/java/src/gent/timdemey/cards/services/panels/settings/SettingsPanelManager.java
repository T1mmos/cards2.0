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
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.payload.P_SaveState;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.action.ActionBase;
import gent.timdemey.cards.services.context.IExecutionListener;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptors;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IActionService;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.panels.PanelManagerBase;
import gent.timdemey.cards.ui.PanelBase;
import net.miginfocom.swing.MigLayout;

public class SettingsPanelManager extends PanelManagerBase
{
    private PanelBase contentPanel;
    private IExecutionListener execListener;
    
    private JTextField tf_pname;
    private JTextField tf_serverUdpPort;
    private JTextField tf_serverTcpPort;
    private JTextField tf_clientUdpPort;    
    
    @Override
    public void preload()
    {
        
    }

    private void updateUI()
    {
        IContextService ctxtServ = Services.get(IContextService.class);
        ReadOnlyState state = ctxtServ.getThreadContext().getReadOnlyState();
        
        tf_pname.setText(state.getLocalName());        
        tf_serverTcpPort.setText("" + state.getConfiguration().getServerTcpPort());       
        tf_serverUdpPort.setText("" + state.getConfiguration().getServerUdpPort());       
        tf_clientUdpPort.setText("" + state.getConfiguration().getClientUdpPort());    
    }
    
    private void onCommandExecuted(CommandBase cmd)
    {
        updateUI();
    }
    
    @Override
    public PanelBase createPanel()
    {
        if (contentPanel == null)
        {
            contentPanel = new PanelBase(new MigLayout(), PanelDescriptors.Settings.id);
            
            JPanel floatPanel = new JPanel(new MigLayout("inset 10"));
            floatPanel.setBackground(new Color(50,50,50,150));
            contentPanel.add(floatPanel, "hmin 200, wmin 350, push, alignx center, aligny center");
                                    
            // player name
            {
                JLabel lbl_pname = new JLabel(Loc.get(LocKey.Label_playername));
                floatPanel.add(lbl_pname, "");                
                tf_pname = new JTextField(30);
                floatPanel.add(tf_pname, "gapright 100, wrap");
            }
            
            // server configuration
            {
                JLabel lbl_tcpport = new JLabel(Loc.get(LocKey.Label_serverTcpPort));
                floatPanel.add(lbl_tcpport, "");
                tf_serverTcpPort = new JTextField(6);
                floatPanel.add(tf_serverTcpPort, "wrap");
                
                JLabel lbl_udpport = new JLabel(Loc.get(LocKey.Label_serverUdpPort));
                floatPanel.add(lbl_udpport, "");
                tf_serverUdpPort = new JTextField(6);
                floatPanel.add(tf_serverUdpPort, "wrap");
            }
            
            // client configuration
            {
                JLabel lbl_udpport = new JLabel(Loc.get(LocKey.Label_clientUdpPort));
                floatPanel.add(lbl_udpport, "");
                tf_clientUdpPort = new JTextField(6);
                floatPanel.add(tf_clientUdpPort, "wrap");
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
    
    @Override
    public void onShown()
    {
        updateUI();
        
        // register listener
        execListener = this::onCommandExecuted;
        IContextService ctxtServ = Services.get(IContextService.class);
        ctxtServ.getThreadContext().addExecutionListener(execListener);
    }
    
    @Override
    public void onHidden()
    {
        // deregister listener
        IContextService ctxtServ = Services.get(IContextService.class);
        ctxtServ.getThreadContext().removeExecutionListener(execListener);
        execListener = null;
    }
        
    private P_SaveState getPayload()
    {
        P_SaveState payload = new P_SaveState();
        payload.id = UUID.randomUUID();
        payload.playerName = tf_pname.getText();
        payload.serverTcpPort = parse(tf_serverTcpPort.getText());
        payload.serverUdpPort = parse(tf_serverUdpPort.getText());
        payload.clientUdpPort = parse(tf_clientUdpPort.getText());
        
        return payload;
    }    
    
    private int parse (String text)
    {
        try 
        {
            int value = Integer.parseInt(text);
            return value;
        }
        catch (Exception ex)
        {
            Logger.warn("Cannot parse '%s' (serverTcpPort) as an integer");
            return -1;
        }
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
