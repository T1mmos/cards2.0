package gent.timdemey.cards.ui.actions;

import javax.swing.Action;
import javax.swing.KeyStroke;

public class ActionDef
{
    public final Action action;
    public final KeyStroke accelerator;
    
    ActionDef(Action action, String accelerator)
    {
        this.action = action;
        this.accelerator = accelerator != null ? KeyStroke.getKeyStroke(accelerator) : null;
    }
}
