package gent.timdemey.cards.ui.actions;

public interface IActionService
{
    public boolean canExecuteAction(String id);

    public void executeAction(String id);
}
