package gent.timdemey.cards.ui.components.drawers;

import javax.swing.JComponent;

import com.alee.laf.button.WebButton;
import com.alee.managers.style.StyleId;

import gent.timdemey.cards.ui.components.swing.JSButton;

public class ButtonDrawer extends DrawerBase<JSButton>
{
    public final void setDecorated(boolean decorated)
    {
        JComponent jcomp = getJComponent();
        
        if (jcomp instanceof WebButton)
        {
            WebButton webb = ((WebButton) jcomp);
            if (!decorated)
            {
                webb.setStyleId(StyleId.buttonUndecorated);    
            }
            else
            {
                webb.resetStyleId();    
            }
        }
    }
}
