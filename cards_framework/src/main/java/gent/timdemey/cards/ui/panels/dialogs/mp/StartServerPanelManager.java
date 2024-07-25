package gent.timdemey.cards.ui.panels.dialogs.mp;

import gent.timdemey.cards.di.Container;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.contract.descriptors.PanelButtonDescriptor;
import gent.timdemey.cards.services.contract.descriptors.PanelButtonDescriptors;
import gent.timdemey.cards.ui.components.swing.JSLayeredPane;
import gent.timdemey.cards.ui.panels.DataPanelManagerBase;
import net.miginfocom.swing.MigLayout;

public class StartServerPanelManager extends DataPanelManagerBase<String, StartServerPanelData> implements DocumentListener
{    
    private JTextField tf_srvname;
    private JTextField tf_srvmsg;
    private JSLayeredPane contentPanel;
        
    public StartServerPanelManager(Container container)
    {
        super(container);
    }
        
    @Override
    public JSLayeredPane createPanel()
    {
        this.tf_srvname = new JTextField(30);
        this.tf_srvmsg = new JTextField(30); 
        this.contentPanel = _JSFactory.createLayeredPane(ComponentTypes.PANEL); 
        this.contentPanel.setLayout(new MigLayout("insets 0, align center center"));

        JLabel lb_srvname = new JLabel(_Loc.get(LocKey.Label_servername));
        JLabel lb_srvmsg = new JLabel(_Loc.get(LocKey.Label_servermsg));

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
    public JSLayeredPane getPanel()
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
        return _Loc.get(LocKey.DialogTitle_creategame);
    }
}
