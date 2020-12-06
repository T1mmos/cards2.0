package gent.timdemey.cards.services.dialogs;

import java.util.EnumSet;

import javax.swing.JPanel;

public abstract class DialogContentCreator<IN, OUT> implements IDialogContentCreator<IN, OUT>
{
    protected static final EnumSet<DialogButtonType> SET_OK_CANCEL = EnumSet.of(DialogButtonType.Cancel, DialogButtonType.Ok);
    protected static final EnumSet<DialogButtonType> SET_CANCEL = EnumSet.of(DialogButtonType.Cancel);
    protected static final EnumSet<DialogButtonType> SET_OK = EnumSet.of(DialogButtonType.Ok);
    
    protected DialogInData<IN> inData;

    public DialogContentCreator()
    {
    }
    
    @Override
    public final JPanel createContent(DialogInData<IN> inData)
    {
        this.inData = inData;
        return createContent();
    }    
    protected abstract JPanel createContent();
    
    protected final void verify(DialogButtonType dbType)
    {
        inData.verifyButtonFunc.accept(dbType);
    }
    
    protected final void close()
    {
        inData.closeFunc.run();
    }
}