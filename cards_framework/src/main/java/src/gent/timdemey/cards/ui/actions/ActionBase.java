package gent.timdemey.cards.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import gent.timdemey.cards.Services;

class ActionBase extends AbstractAction
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
    
    @Override
    public boolean isEnabled()
    {
        // TODO Auto-generated method stub
        return super.isEnabled();
    }
}
