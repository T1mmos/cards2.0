package gent.timdemey.cards.ui.actions;

import javax.swing.KeyStroke;

public class ActionDef
{
    public final ActionBase action;
    public final KeyStroke accelerator;
    
    ActionDef(ActionBase action, String accelerator)
    {
        this.action = action;
        this.accelerator = accelerator != null ? KeyStroke.getKeyStroke(accelerator) : null;
    }
}
