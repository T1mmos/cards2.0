package gent.timdemey.cards.ui.actions;

public class SolShowTestActionService extends ActionService
{
    @Override
    public boolean canExecuteAction(String id)
    {
        if (id.equals(SolShowTestActions.ACTION_FAKESOLSHOWGAME))
        {
            return true;
        }
        
        return super.canExecuteAction(id);
    }
    
    @Override
    public void executeAction(String id)
    {
        switch (id)
        {
            case SolShowTestActions.ACTION_FAKESOLSHOWGAME:
                //

            default:
                super.executeAction(id);
        }
    }
}
