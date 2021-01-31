package gent.timdemey.cards.services.panels.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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
import gent.timdemey.cards.services.contract.descriptors.PanelButtonDescriptor;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IActionService;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.panels.DataPanelManagerBase;
import gent.timdemey.cards.ui.PanelBase;
import net.miginfocom.swing.MigLayout;

public class SettingsPanelManager extends  DataPanelManagerBase<Void, Void>
{
    private PanelBase contentPanel;
    private IExecutionListener execListener;
    
    private JTextField tf_pname;
    private JTextField tf_serverUdpPort;
    private JTextField tf_serverTcpPort;
    private JTextField tf_clientUdpPort;    
    
    private DocumentListener docListener = null;
    private PanelButtonDescriptor pbd_savecfg;
    
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
            floatPanel.setOpaque(false);
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
            
            docListener = new DocumentListener()
            {
                
                @Override
                public void removeUpdate(DocumentEvent e)
                {
                    inData.verifyButtonFunc.accept(pbd_savecfg);
                }
                
                @Override
                public void insertUpdate(DocumentEvent e)
                {
                    inData.verifyButtonFunc.accept(pbd_savecfg);
                }
                
                @Override
                public void changedUpdate(DocumentEvent e)
                {
                    inData.verifyButtonFunc.accept(pbd_savecfg);
                }
            };
        }
        
        return contentPanel;
    }
    
    @Override
    public void onShown()
    {
        updateUI();
        
        tf_pname.getDocument().addDocumentListener(docListener);
        tf_serverTcpPort.getDocument().addDocumentListener(docListener);
        tf_serverUdpPort.getDocument().addDocumentListener(docListener);
        tf_clientUdpPort.getDocument().addDocumentListener(docListener);
        
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
        
        tf_pname.getDocument().removeDocumentListener(docListener);
        tf_serverTcpPort.getDocument().removeDocumentListener(docListener);
        tf_serverUdpPort.getDocument().removeDocumentListener(docListener);
        tf_clientUdpPort.getDocument().removeDocumentListener(docListener);
    }
        
    private P_SaveState getSettings()
    {
        P_SaveState settings = new P_SaveState();
        
        settings.id = UUID.randomUUID();
        settings.playerName = tf_pname.getText();
        settings.serverTcpPort = parse(tf_serverTcpPort.getText());
        settings.serverUdpPort = parse(tf_serverUdpPort.getText());
        settings.clientUdpPort = parse(tf_clientUdpPort.getText());
        
        return settings;
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
            Logger.warn("Cannot parse '%s' (serverTcpPort) as an integer", text);
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

    @Override
    public List<PanelButtonDescriptor> getButtonTypes()
    {
        IActionService actServ = Services.get(IActionService.class);               
        List<PanelButtonDescriptor> buttons = new ArrayList<>();
        
        // to menu 
        {
            ActionBase act_tomenu = actServ.getAction(ActionDescriptors.SHOWMENU);            
            PanelButtonDescriptor desc = new PanelButtonDescriptor(act_tomenu);
            buttons.add(desc);
        }
        
        // save settings
        {
            P_SaveState payload = new P_SaveState();
            payload.id = UUID.randomUUID();         
            ActionBase act_savecfg = actServ.getAction(ActionDescriptors.SAVECFG, this::getSettings);               
            pbd_savecfg = new PanelButtonDescriptor(act_savecfg);
            buttons.add(pbd_savecfg);
        }
        
        return buttons;
    }

    @Override
    public Void onClose(PanelButtonDescriptor dbType)
    {
        // all buttons have actions, no additional logic to do
        return null;
    }

    @Override
    public boolean isButtonEnabled(PanelButtonDescriptor dbType)
    {
        return true;
    }

    @Override
    public String createTitle()
    {
        return Loc.get(LocKey.Action_showsettings);
    }

}
