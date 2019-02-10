package gent.timdemey.cards.services.dialogs;

import java.util.EnumSet;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.logging.ILogManager;

public final class DialogService implements IDialogService {
    
    
    private final JFrame frame;
    
    public DialogService (JFrame frame)
    {
        this.frame = frame;
    }
    
    @Override
    public void ShowNotImplemented()
    {
        ShowMessage("Not implemented", "This feature is not implemented yet.");
    }
    
    @Override
    public DialogData<Void> ShowMessage(String title, String message)
    {
        MessageDialogContent dialogContent = new MessageDialogContent();
        return ShowAdvanced(title, message, dialogContent, DialogButtonType.Ok);
    }    

    @Override
    public DialogData<Void> ShowInternalError() {
        MessageDialogContent dialogContent = new MessageDialogContent();
        String title = Loc.get("general_internal_error_title");
        String msg =  Loc.get("general_internal_error_msg");
        return ShowAdvanced(title, msg, dialogContent, DialogButtonType.Ok);
    }
    
    @Override
    public <IN,OUT> DialogData<OUT> ShowAdvanced(String title, IN data, DialogContent<IN,OUT> dialogContent, DialogButtonType closeType)
    {
        return ShowAdvanced(title, data, dialogContent, EnumSet.of(closeType));
    }
    
    @Override
    public <IN,OUT> DialogData<OUT> ShowAdvanced(String title, IN data, DialogContent<IN,OUT> dialogContent, EnumSet<DialogButtonType> closeTypes)
    {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());
        Services.get(ILogManager.class).log("Showing dialog with title: " + title);
        
        JDialog dialog = new JDialog(frame, title, true);
        return dialogContent.show(dialog, data, closeTypes);
    }

}


