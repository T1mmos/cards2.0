package gent.timdemey.cards.services.dialogs;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.services.interfaces.IDialogService;
import gent.timdemey.cards.services.interfaces.IFrameService;
import net.miginfocom.swing.MigLayout;

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
    public DialogOutData<Void> ShowMessage(String title, String message)
    {
        MessageDialogContent dialogContent = new MessageDialogContent();
        return ShowAdvanced(title, message, dialogContent);
    }

    @Override
    public DialogOutData<Void> ShowInternalError()
    {
        MessageDialogContent dialogContent = new MessageDialogContent();
        String title = Loc.get(LocKey.DialogTitle_generalerror);
        String msg = Loc.get(LocKey.DialogMessage_generalerror);
        return ShowAdvanced(title, msg, dialogContent);
    }    
    
    @Override
    public <IN, OUT> DialogOutData<OUT> ShowAdvanced(String title, IN data, DialogContentCreator<IN, OUT> contentCreator)
    {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());
        Logger.info("Showing dialog with title: " + title);        

        // create a dialog
        IFrameService frameServ = Services.get(IFrameService.class);
        JFrame frame = frameServ.getFrame();
        JDialog dialog = new JDialog(frame, title, true);
        
        // create a reference to the out data of the dialog
        DialogOutData<OUT> outData = new DialogOutData<>();
        
        // create the buttons that need to be shown according to the content creator
        EnumSet<DialogButtonType> dbTypes = contentCreator.getButtonTypes();
        Map<DialogButtonType, JButton> buttons = new HashMap<>();        
        for (DialogButtonType dbType : dbTypes)
        {
            ActionListener hehe = (e) -> 
            {
                OUT out = contentCreator.onClose(dbType);
                outData.data_out = out;
                outData.closeType = dbType;
                
                dialog.setVisible(false);
            };
            JButton button = new JButton(dbType.loctext); 
            
            button.addActionListener(hehe);
            button.setMinimumSize(new Dimension(75, 20));
            
            buttons.put(dbType, button);            
        }
        
        // prepare the incoming data
        DialogInData<IN> inData = new DialogInData<>();
        inData.data_in = data;
        inData.verifyButtonFunc = (btnType) ->
        {
            JButton button = buttons.get(btnType);
            if (button == null)
            {
                return;
            }
            
            boolean enabled = contentCreator.isButtonEnabled(btnType);
            button.setEnabled(enabled);
        };
        
        // create the entire panel, add the custom content and the buttons
        JPanel allContent = new JPanel(new MigLayout("insets 5"));        
        JPanel customContent = contentCreator.createContent(inData);
        allContent.add(customContent, "grow, push, wrap");
        String mig_first = "span, split " + dbTypes.size() + ", pushx, align center, sg buts";
        String mig_sg = "sg buts";
        int cnt = 0;
        for (DialogButtonType dbType : dbTypes)
        {           
            JButton button = buttons.get(dbType);
            allContent.add(button, cnt++ == 0 ? mig_first : mig_sg);
            
            // enabled/disable the button
            inData.verifyButtonFunc.accept(dbType);
        }
                
        // further prepare the dialog and show it
        dialog.setMinimumSize(new Dimension(300, 150));
        dialog.setContentPane(allContent);
        dialog.pack();
        dialog.setLocationRelativeTo(dialog.getParent());
        dialog.setResizable(false);
        contentCreator.onShow();
        dialog.setVisible(true);
        
        // closed via X-button -> none of the button callbacks was called 
        if (outData.closeType == null)
        {   
            OUT payload = contentCreator.onClose(DialogButtonType.Cancel);
            outData.data_out = payload;
            outData.closeType = DialogButtonType.Cancel;
        }
        
        // finally return the out data
        return outData;
    }

}
