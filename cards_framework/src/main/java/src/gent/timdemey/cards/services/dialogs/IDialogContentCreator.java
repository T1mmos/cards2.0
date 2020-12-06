package gent.timdemey.cards.services.dialogs;

import java.util.EnumSet;

import javax.swing.JPanel;

public interface IDialogContentCreator<IN, OUT>
{
    public EnumSet<DialogButtonType> getButtonTypes();
    public JPanel createContent(DialogInData<IN> inData);   
    public void onShow(); 
    public OUT onClose(DialogButtonType dbType);
    public boolean isButtonEnabled(DialogButtonType dbType);
}
