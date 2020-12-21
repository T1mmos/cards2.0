package gent.timdemey.cards.ui.panels;

import java.util.EnumSet;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.services.panels.DataPanelManagerBase;
import gent.timdemey.cards.services.panels.PanelButtonType;
import gent.timdemey.cards.services.panels.PanelInData;
import net.miginfocom.swing.MigLayout;

public class StartServerPanelCreator extends DataPanelManagerBase<Void, StartServerPanelData> implements DocumentListener
{
    private final JTextField tf_srvname = new JTextField(30);
    private final JTextField tf_srvmsg = new JTextField(30);
    private final JTextField tf_pname = new JTextField(30);
    // private final JCheckBox cb_autoconnect = new JCheckBox(Loc.get(LocKey.CheckBox_autoconnect));
        
    public StartServerPanelCreator(String initialPname)
    {
        tf_pname.setText(initialPname);
    }
    
    @Override
    public JPanel create(PanelInData<Void> data)
    {
        JPanel panel = new JPanel(new MigLayout("insets 0"));

        JLabel lb_srvname = new JLabel(Loc.get(LocKey.Label_servername));
        JLabel lb_srvmsg = new JLabel(Loc.get(LocKey.Label_servermsg));
        JLabel lb_pname = new JLabel(Loc.get(LocKey.Label_playername));

        panel.add(lb_srvname, "");
        panel.add(tf_srvname, "wrap");
        panel.add(lb_srvmsg, "");
        panel.add(tf_srvmsg, "wrap");
        panel.add(lb_pname, "");
        panel.add(tf_pname, "wrap");
        // panel.add(cb_autoconnect, "span, pushx, align left");
        
        tf_srvname.getDocument().addDocumentListener(this);
        tf_pname.getDocument().addDocumentListener(this);

        return panel;
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
    public void onShown()
    {
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
}
