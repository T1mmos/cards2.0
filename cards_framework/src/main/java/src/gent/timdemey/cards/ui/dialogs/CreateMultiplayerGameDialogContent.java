package gent.timdemey.cards.ui.dialogs;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.entities.commands.C_CreateServer;
import gent.timdemey.cards.services.dialogs.DialogButtonType;
import gent.timdemey.cards.services.dialogs.DialogContent;
import net.miginfocom.swing.MigLayout;

public class CreateMultiplayerGameDialogContent extends DialogContent<Void, C_CreateServer> implements DocumentListener
{

    private final JTextField tf_srvname = new JTextField(30);
    private final JTextField tf_srvmsg = new JTextField(30);

    @Override
    protected JPanel createContent(Void parameter)
    {
        JPanel panel = new JPanel(new MigLayout("insets 0"));

        JLabel lb_srvname = new JLabel(Loc.get(LocKey.Label_servername));
        JLabel lb_srvmsg = new JLabel(Loc.get(LocKey.Label_servermsg));

        panel.add(lb_srvname, "");
        panel.add(tf_srvname, "wrap");
        panel.add(lb_srvmsg, "");
        panel.add(tf_srvmsg, "wrap");

        tf_srvname.getDocument().addDocumentListener(this);

        return panel;
    }

    @Override
    protected C_CreateServer onClose(DialogButtonType dbType)
    {
        if (dbType == DialogButtonType.Ok)
        {
            return new C_CreateServer(tf_srvname.getText(), tf_srvmsg.getText(), 9000, 9010, 2, 2); // should override
                                                                                                    // this class to
                                                                                                    // specify other
                                                                                                    // stuff
        }
        else
        {
            return null;
        }
    }

    @Override
    protected boolean isOk()
    {
        return !tf_srvname.getText().isEmpty();
    }

    @Override
    public void insertUpdate(DocumentEvent e)
    {
        checkOk();
    }

    @Override
    public void removeUpdate(DocumentEvent e)
    {
        checkOk();
    }

    @Override
    public void changedUpdate(DocumentEvent e)
    {
        checkOk();
    }

    @Override
    protected void onShow()
    {

    }
}
