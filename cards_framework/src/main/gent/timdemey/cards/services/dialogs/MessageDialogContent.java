package gent.timdemey.cards.services.dialogs;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class MessageDialogContent extends DialogContent<String, Void> {
    
    @Override
    protected JPanel createContent(String parameter) {
        JPanel content = new JPanel(new MigLayout("insets 0"));
        
        content.add(new JLabel(parameter), "push, wrap");
        
        return content;
    }

    @Override
    protected Void onClose(DialogButtonType dbType) {
        return null;
    }

    @Override
    protected void onShow() {
        
    }
}
