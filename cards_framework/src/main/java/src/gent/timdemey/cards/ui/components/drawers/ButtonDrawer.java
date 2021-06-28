package gent.timdemey.cards.ui.components.drawers;

import java.awt.Color;

import javax.swing.JComponent;

import com.alee.laf.button.WebButton;
import com.alee.managers.style.StyleId;

import gent.timdemey.cards.ui.components.swing.JSButton;
import gent.timdemey.cards.ui.components.swing.JSLayeredPane;

public class ButtonDrawer extends DrawerBase<JSButton>
{
    public final void setDecorated(boolean decorated)
    {
        JComponent jcomp = getJComponent();
        
        JSLayeredPane parent = (JSLayeredPane)jcomp.getParent();
        if (parent != null)
        {
            Color bg = parent.getBackground();
            boolean isOpaque = parent.isOpaque();
            IDrawer drawer =  parent.getDrawer();
        }
        
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
