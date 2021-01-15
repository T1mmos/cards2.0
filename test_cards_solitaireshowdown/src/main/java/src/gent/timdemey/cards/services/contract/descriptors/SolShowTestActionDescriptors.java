package gent.timdemey.cards.services.contract.descriptors;

public final class SolShowTestActionDescriptors
{
    public static final String FAKESOLSHOWGAME = "action.fakesolshowgame";
    public static final String SWITCHCARDSVISIBLE = "action.switchcardsvisible";
    
    public static final ActionDescriptor ad_fakegame = new ActionDescriptor(FAKESOLSHOWGAME);
    public static final ActionDescriptor ad_switchcvis = new ActionDescriptor(SWITCHCARDSVISIBLE);
}
