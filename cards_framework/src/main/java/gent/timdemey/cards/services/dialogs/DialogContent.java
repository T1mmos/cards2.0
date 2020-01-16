package gent.timdemey.cards.services.dialogs;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.EnumSet;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public abstract class DialogContent<IN, OUT>
{

    private DialogData<OUT> data;

    private JButton button_ok = null;
    private JDialog dialog;

    public DialogContent()
    {
    }

    DialogData<OUT> show(JDialog dialog, IN parameter, EnumSet<DialogButtonType> dbTypes)
    {
        this.dialog = dialog;

        JPanel allContent = new JPanel(new MigLayout("insets 5"));
        JPanel customContent = createContent(parameter);

        allContent.add(customContent, "grow, push, wrap");

        String mig_first = "span, split " + dbTypes.size() + ", pushx, align center, sg buts";
        String mig_sg = "sg buts";

        int cnt = 0;
        for (DialogButtonType dbType : dbTypes)
        {
            JButton button = new JButton(new CloseAction(dialog, dbType));
            if (dbType == DialogButtonType.Ok)
            {
                button_ok = button;
                dialog.getRootPane().setDefaultButton(button_ok);
            }
            button.setMinimumSize(new Dimension(75, 20));
            allContent.add(button, cnt++ == 0 ? mig_first : mig_sg);
        }

        checkOk();

        dialog.setMinimumSize(new Dimension(300, 150));
        dialog.setContentPane(allContent);
        dialog.pack();
        dialog.setLocationRelativeTo(dialog.getParent());
        dialog.setResizable(false);

        onShow();

        dialog.setVisible(true);

        if (data == null)
        { // close via X-button
            OUT payload = onClose(DialogButtonType.Cancel);
            data = new DialogData<OUT>(DialogButtonType.Cancel, payload);
        }

        return data;
    }

    protected final void checkOk()
    {
        if (button_ok != null)
        {
            button_ok.setEnabled(isOk());
        }
    }

    protected boolean isOk()
    {
        return true; // by default, can always click ok
    }

    protected abstract JPanel createContent(IN parameter);

    protected abstract void onShow();

    protected abstract OUT onClose(DialogButtonType dbType);

    protected final void close()
    {
        data = new DialogData<OUT>(DialogButtonType.Forced, null);
        dialog.setVisible(false);
    }

    private class CloseAction extends AbstractAction
    {
        private final DialogButtonType dbType;
        private final JDialog dialog;

        private CloseAction(JDialog dialog, DialogButtonType dbType)
        {
            super(dbType.loctext);
            this.dialog = dialog;
            this.dbType = dbType;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            OUT payload = onClose(dbType);
            data = new DialogData<OUT>(dbType, payload);
            dialog.setVisible(false);
        }
    }
}
