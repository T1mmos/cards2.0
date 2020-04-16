package gent.timdemey.cards.ui.actions;

public class SolShowTestActionFactory extends ActionFactory
{
    private ActionDef ad_fakesolshowgame = new ActionDef(new A_DebugFakeSolShowGame(), "ctrl F");
    
    @Override
    public ActionDef getActionDef(String action)
    {
        if (action.equals(SolShowTestActions.ACTION_FAKESOLSHOWGAME))
        {
            return ad_fakesolshowgame;
        }
        
        return super.getActionDef(action);
    }
}
