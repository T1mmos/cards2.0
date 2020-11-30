package gent.timdemey.cards.services.dialogs;

import java.util.EnumSet;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.services.interfaces.IDialogService;
import gent.timdemey.cards.services.interfaces.IFrameService;

public final class DialogService implements IDialogService
{
    public DialogService()
    {
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
    public DialogData<Void> ShowInternalError()
    {
        MessageDialogContent dialogContent = new MessageDialogContent();
        String title = Loc.get(LocKey.DialogTitle_generalerror);
        String msg = Loc.get(LocKey.DialogMessage_generalerror);
        return ShowAdvanced(title, msg, dialogContent, DialogButtonType.Ok);
    }

    @Override
    public <IN, OUT> DialogData<OUT> ShowAdvanced(String title, IN data, DialogContent<IN, OUT> dialogContent,
            DialogButtonType closeType)
    {
        return ShowAdvanced(title, data, dialogContent, EnumSet.of(closeType));
    }

    @Override
    public <IN, OUT> DialogData<OUT> ShowAdvanced(String title, IN data, DialogContent<IN, OUT> dialogContent,
            EnumSet<DialogButtonType> closeTypes)
    {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());
        Logger.info("Showing dialog with title: " + title);
        
        IFrameService frameServ = Services.get(IFrameService.class);
        JFrame frame = frameServ.getFrame();
        JDialog dialog = new JDialog(frame, title, true);
        DialogData<OUT> outData = dialogContent.show(dialog, data, closeTypes);
        
        return outData;
    }

    
}
