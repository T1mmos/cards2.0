package gent.timdemey.cards.services.panels.mp;

import java.util.EnumSet;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.panels.DataPanelManagerBase;
import gent.timdemey.cards.services.panels.PanelBase;
import gent.timdemey.cards.services.panels.PanelButtonType;
import net.miginfocom.swing.MigLayout;

public class StartServerPanelManager extends DataPanelManagerBase<Void, StartServerPanelData> implements DocumentListener
{
    private JTextField tf_srvname;
    private JTextField tf_srvmsg;
    private JTextField tf_pname;
    private PanelBase contentPanel;
        
    public StartServerPanelManager(String initialPname)
    {
        tf_pname.setText(initialPname);
    }
    
    @Override
    public PanelBase create()
    {
        this.tf_srvname = new JTextField(30);
        this.tf_srvmsg = new JTextField(30);
        this.tf_pname = new JTextField(30);
        this.contentPanel = new PanelBase(PanelDescriptors.START_SERVER, new MigLayout("insets 0"));

        JLabel lb_srvname = new JLabel(Loc.get(LocKey.Label_servername));
        JLabel lb_srvmsg = new JLabel(Loc.get(LocKey.Label_servermsg));
        JLabel lb_pname = new JLabel(Loc.get(LocKey.Label_playername));

        contentPanel.add(lb_srvname, "");
        contentPanel.add(tf_srvname, "wrap");
        contentPanel.add(lb_srvmsg, "");
        contentPanel.add(tf_srvmsg, "wrap");
        contentPanel.add(lb_pname, "");
        contentPanel.add(tf_pname, "wrap");
        // panel.add(cb_autoconnect, "span, pushx, align left");
        
        tf_srvname.getDocument().addDocumentListener(this);
        tf_pname.getDocument().addDocumentListener(this);

        return contentPanel;
    }

    @Override
    public StartServerPanelData onClose(PanelButtonType dbType)
    {
        tf_srvname.getDocument().removeDocumentListener(this);
        tf_pname.getDocument().removeDocumentListener(this);
        
        if (dbType == PanelButtonType.Ok)
        {
            boolean autoconnect = true; // we currently don't support starting a server without automatically being a player
            return new StartServerPanelData(tf_pname.getText(), tf_srvname.getText(), tf_srvmsg.getText(), 9000, 9010, autoconnect);
        }
        else
        {
            return null;
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e)
    {
        verify(PanelButtonType.Ok);
    }

    @Override
    public void removeUpdate(DocumentEvent e)
    {
        verify(PanelButtonType.Ok);
    }

    @Override
    public void changedUpdate(DocumentEvent e)
    {
        verify(PanelButtonType.Ok);
    }

    @Override
    public EnumSet<PanelButtonType> getButtonTypes()
    {
        return SET_OK_CANCEL;
    }

    @Override
    public boolean isButtonEnabled(PanelButtonType dbType)
    {
        if (dbType == PanelButtonType.Ok)
        {
            return !tf_srvname.getText().trim().isEmpty() && !tf_pname.getText().trim().isEmpty();
        }        
        else
        {
            return true;
        }
    }

    @Override
    public PanelBase get()
    {
        return contentPanel;
    }

    @Override
    public void destroy()
    {
        contentPanel = null;
    }
}
