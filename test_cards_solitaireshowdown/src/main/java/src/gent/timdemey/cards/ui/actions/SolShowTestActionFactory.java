package gent.timdemey.cards.ui.actions;

public class SolShowTestActionFactory extends ActionFactory
{
    private ActionDef ad_fakesolshowgame = new ActionDef(new A_DebugFakeSolShowGame(), "ctrl F");
    private ActionDef ad_switchcardsvisible = new ActionDef(new A_DebugSwitchCardsVisible(), "ctrl V");

    @Override
    public ActionDef getActionDef(String action)
    {
        switch (action)
        {
            case SolShowTestActions.ACTION_FAKESOLSHOWGAME:
                return ad_fakesolshowgame;
            case SolShowTestActions.ACTION_SWITCHCARDSVISIBLE:
                return ad_switchcardsvisible;
            default:
                return super.getActionDef(action);
        }
    }
}
