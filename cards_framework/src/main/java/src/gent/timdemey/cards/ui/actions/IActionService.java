package gent.timdemey.cards.ui.actions;

import javax.swing.Action;

public interface IActionService
{
    public boolean canExecuteAction(ActionDescriptor desc);

    public void executeAction(ActionDescriptor desc);    
    
    public Action getAction(ActionDescriptor desc);
}
