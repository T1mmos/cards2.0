package gent.timdemey.cards.services.panels.dialogs.mp;

import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.services.contract.descriptors.PanelButtonDescriptor;
import gent.timdemey.cards.services.contract.descriptors.PanelButtonDescriptors;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.panels.DataPanelManagerBase;
import gent.timdemey.cards.ui.PanelBase;
import net.miginfocom.swing.MigLayout;

public class StartServerPanelManager extends DataPanelManagerBase<String, StartServerPanelData> implements DocumentListener
{    
    private JTextField tf_srvname;
    private JTextField tf_srvmsg;
    private PanelBase contentPanel;
        
    public StartServerPanelManager()
    {
    }
        
    @Override
    public PanelBase createPanel()
    {
        this.tf_srvname = new JTextField(30);
        this.tf_srvmsg = new JTextField(30); 
        this.contentPanel = new PanelBase(new MigLayout("insets 0, align center center"), PanelDescriptors.StartServer.id + " content");

        JLabel lb_srvname = new JLabel(Loc.get(LocKey.Label_servername));
        JLabel lb_srvmsg = new JLabel(Loc.get(LocKey.Label_servermsg));

        contentPanel.add(lb_srvname, "");
        contentPanel.add(tf_srvname, "wrap");
        contentPanel.add(lb_srvmsg, "");
        contentPanel.add(tf_srvmsg, "wrap");
       
        return contentPanel;
    }
    
    @Override
    public void onShown()
    {
        tf_srvname.getDocument().addDocumentListener(this);
    }

    @Override
    public void onHidden()
    {
        tf_srvname.getDocument().removeDocumentListener(this);
    }
    
    @Override
    public StartServerPanelData onClose(PanelButtonDescriptor dbType)
    {
        if (dbType == PanelButtonDescriptors.Ok)
        {
            boolean autoconnect = true; // we currently don't support starting a server without automatically being a player
            return new StartServerPanelData(tf_srvname.getText(), tf_srvmsg.getText(), autoconnect);
        }
        else
        {
            return null;
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e)
    {
        verify(PanelButtonDescriptors.Ok);
    }

    @Override
    public void removeUpdate(DocumentEvent e)
    {
        verify(PanelButtonDescriptors.Ok);
    }

    @Override
    public void changedUpdate(DocumentEvent e)
    {
        verify(PanelButtonDescriptors.Ok);
    }

    @Override
    public List<PanelButtonDescriptor> getButtonTypes()
    {
        return SET_OK_CANCEL;
    }

    @Override
    public boolean isButtonEnabled(PanelButtonDescriptor dbType)
    {
        if (dbType == PanelButtonDescriptors.Ok)
        {
            return !tf_srvname.getText().trim().isEmpty();
        }        
        else
        {
            return true;
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
    public String createTitle()
    {
        return Loc.get(LocKey.DialogTitle_creategame);
    }
}
