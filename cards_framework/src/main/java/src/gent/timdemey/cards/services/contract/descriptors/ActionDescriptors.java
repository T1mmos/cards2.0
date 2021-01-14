package gent.timdemey.cards.services.contract.descriptors;

public class ActionDescriptors
{
    private static final String ACTION_ABOUT = "action.game.about";
    private static final String ACTION_DEBUG_DRAW = "action.debug.drawoutlines";
    private static final String ACTION_DEBUG_GC = "action.debug.gc";
    private static final String ACTION_CREATE_MP = "action.game.createmp";
    private static final String ACTION_JOIN_MP = "action.game.join";
    private static final String ACTION_LEAVE_MP = "action.leavemp";
    private static final String ACTION_MINIMIZE = "action.frame.minimize";
    private static final String ACTION_MAXIMIZE = "action.frame.maximize";
    private static final String ACTION_UNMAXIMIZE = "action.frame.unmaximize";
    private static final String ACTION_QUIT = "action.app.quit";
    private static final String ACTION_REDO = "action.game.redo";
    private static final String ACTION_SHOWMENU = "action.game.showmenu";
    private static final String ACTION_TOGGLEMENU_MP = "action.game.togglemenump";
    private static final String ACTION_START_SP = "action.game.start";
    private static final String ACTION_START_MP = "action.game.startmp";
    private static final String ACTION_UNDO = "action.game.undo";

    public static final ActionDescriptor ad_about = new ActionDescriptor(ACTION_ABOUT);
    public static final ActionDescriptor ad_createmp = new ActionDescriptor(ACTION_CREATE_MP);
    public static final ActionDescriptor ad_debugdraw = new ActionDescriptor(ACTION_DEBUG_DRAW);
    public static final ActionDescriptor ad_gc = new ActionDescriptor(ACTION_DEBUG_GC);
    public static final ActionDescriptor ad_join = new ActionDescriptor(ACTION_JOIN_MP);
    public static final ActionDescriptor ad_leavemp = new ActionDescriptor(ACTION_LEAVE_MP);
    public static final ActionDescriptor ad_minimize = new ActionDescriptor(ACTION_MINIMIZE);
    public static final ActionDescriptor ad_maximize = new ActionDescriptor(ACTION_MAXIMIZE);
    public static final ActionDescriptor ad_unmaximize = new ActionDescriptor(ACTION_UNMAXIMIZE);
    public static final ActionDescriptor ad_quit = new ActionDescriptor(ACTION_QUIT);
    public static final ActionDescriptor ad_redo = new ActionDescriptor(ACTION_REDO);
    public static final ActionDescriptor ad_showmenu = new ActionDescriptor(ACTION_SHOWMENU);
    public static final ActionDescriptor ad_togglemenump = new ActionDescriptor(ACTION_TOGGLEMENU_MP);
    public static final ActionDescriptor ad_startsp = new ActionDescriptor(ACTION_START_SP);
    public static final ActionDescriptor ad_startmp = new ActionDescriptor(ACTION_START_MP);
    public static final ActionDescriptor ad_undo = new ActionDescriptor(ACTION_UNDO);

    private ActionDescriptors()
    {
    }
}
