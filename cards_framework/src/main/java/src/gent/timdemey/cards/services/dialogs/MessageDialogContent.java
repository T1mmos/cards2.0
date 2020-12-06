package gent.timdemey.cards.services.dialogs;

import java.util.EnumSet;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class MessageDialogContent extends PanelCreatorBase<String, Void>
{
    @Override
    public Void onClose(DialogButtonType dbType)
    {
        return null;
    }

    @Override
    public void onShow()
    {

    }

    @Override
    public EnumSet<DialogButtonType> getButtonTypes()
    {
        return SET_OK;
    }

    @Override
    public boolean isButtonEnabled(DialogButtonType dbType)
    {
        return true;
    }

    @Override
    protected JPanel createContent()
    {
        JPanel content = new JPanel(new MigLayout("insets 0"));

        content.add(new JLabel(inData.data_in), "push, wrap");

        return content;
    }
}
