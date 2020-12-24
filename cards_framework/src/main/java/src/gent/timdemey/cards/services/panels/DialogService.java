package gent.timdemey.cards.services.panels;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
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
import gent.timdemey.cards.services.panels.message.MessagePanelManager;
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
    public PanelOutData<Void> ShowMessage(String title, String message)
    {
        MessagePanelManager dialogContent = new MessagePanelManager();
        return ShowAdvanced(title, message, dialogContent);
    }

    @Override
    public PanelOutData<Void> ShowInternalError()
    {
        MessagePanelManager dialogContent = new MessagePanelManager();
        String title = Loc.get(LocKey.DialogTitle_generalerror);
        String msg = Loc.get(LocKey.DialogMessage_generalerror);
        return ShowAdvanced(title, msg, dialogContent);
    }    
    
    @Override
    public <IN, OUT> PanelOutData<OUT> ShowAdvanced(String title, IN data, IDataPanelManager<IN, OUT> panelMgr)
    {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());
        Logger.info("Showing dialog with title: " + title);        

        // create a dialog
        IFrameService frameServ = Services.get(IFrameService.class);
        JFrame frame = frameServ.getFrame();
        JDialog dialog = new JDialog(frame, title, true);
        
        // create a reference to the out data of the dialog
        PanelOutData<OUT> outData = new PanelOutData<>();
        
        // create the buttons that need to be shown according to the content creator
        EnumSet<PanelButtonType> dbTypes = panelMgr.getButtonTypes();
        Map<PanelButtonType, JButton> buttons = new HashMap<>();        
        for (PanelButtonType dbType : dbTypes)
        {
            ActionListener hehe = (e) -> 
            {
                OUT out = panelMgr.onClose(dbType);
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
        PanelInData<IN> inData = new PanelInData<>();
        inData.data_in = data;
        inData.verifyButtonFunc = (btnType) ->
        {
            JButton button = buttons.get(btnType);
            if (button == null)
            {
                return;
            }
            
            boolean enabled = panelMgr.isButtonEnabled(btnType);
            button.setEnabled(enabled);
        };
        
        // create the entire panel, add the custom content and the buttons
        JPanel allContent = new JPanel(new MigLayout("insets 5"));        
        panelMgr.load(inData);
        JComponent customContent = panelMgr.create();
        allContent.add(customContent, "grow, push, wrap");
        String mig_first = "span, split " + dbTypes.size() + ", pushx, align center, sg buts";
        String mig_sg = "sg buts";
        int cnt = 0;
        for (PanelButtonType dbType : dbTypes)
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
        panelMgr.setVisible(true);
        dialog.setVisible(true);
        
        // closed via X-button -> none of the button callbacks was called 
        if (outData.closeType == null)
        {   
            OUT payload = panelMgr.onClose(PanelButtonType.Cancel);
            outData.data_out = payload;
            outData.closeType = PanelButtonType.Cancel;
        }
        
        // finally return the out data
        return outData;
    }

}
