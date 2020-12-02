package gent.timdemey.cards.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.services.context.IContextListener;

abstract class ActionBase extends AbstractAction implements IContextListener, IStateListener
{
    private final ActionDescriptor desc;

    protected ActionBase(ActionDescriptor desc, String title)
    {
        super(title);

        this.desc = desc;
        checkEnabled();
    }
    
    protected ActionBase(ActionDescriptor desc, String title, Icon icon)
    {
        super(title, icon);

        this.desc = desc;
        checkEnabled();
    }

    @Override
    public final void actionPerformed(ActionEvent e)
    {
        Services.get(IActionService.class).executeAction(desc);
    }

    protected final void checkEnabled()
    {
        setEnabled(Services.get(IActionService.class).canExecuteAction(desc));
    }
}
