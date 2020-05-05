package gent.timdemey.cards.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.services.context.IContextListener;

abstract class ActionBase extends AbstractAction implements IContextListener, IStateListener
{
    private final String action;

    protected ActionBase(String action, String title)
    {
        super(title);
        this.action = action;
        checkEnabled();
    }

    @Override
    public final void actionPerformed(ActionEvent e)
    {
        Services.get(IActionService.class).executeAction(action);
    }

    protected final void checkEnabled()
    {
        setEnabled(Services.get(IActionService.class).canExecuteAction(action));
    }
}
