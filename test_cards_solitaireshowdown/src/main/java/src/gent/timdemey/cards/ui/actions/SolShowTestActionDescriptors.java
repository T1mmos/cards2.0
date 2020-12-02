package gent.timdemey.cards.ui.actions;

public class SolShowTestActionDescriptors
{
    public static final String ACTION_FAKESOLSHOWGAME = "action.fakesolshowgame";
    public static final String ACTION_SWITCHCARDSVISIBLE = "action.switchcardsvisible";
    
    public static final ActionDescriptor ad_fakegame = new ActionDescriptor(ACTION_FAKESOLSHOWGAME, "ctrl F");
    public static final ActionDescriptor ad_switchcvis = new ActionDescriptor(ACTION_SWITCHCARDSVISIBLE, null);
}
