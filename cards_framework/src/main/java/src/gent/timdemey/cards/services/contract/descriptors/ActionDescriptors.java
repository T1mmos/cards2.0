package gent.timdemey.cards.services.contract.descriptors;

public class ActionDescriptors
{
    private static final String ACTION_DEBUG_DRAW = "action.debug.drawoutlines";
    private static final String ACTION_DEBUG_GC = "action.debug.gc";
    private static final String ACTION_CREATE_MP = "action.create";
    private static final String ACTION_JOIN = "action.join";
    private static final String ACTION_LEAVE = "action.leave";
    private static final String ACTION_MINIMIZE = "action.minimize";
    private static final String ACTION_MAXIMIZE = "action.maximize";
    private static final String ACTION_UNMAXIMIZE = "action.unmaximize";
    private static final String ACTION_QUIT = "action.quit";
    private static final String ACTION_REDO = "action.redo";
    private static final String ACTION_START = "action.start";
    private static final String ACTION_START_MP = "action.startmultiplayer";
    private static final String ACTION_UNDO = "action.undo";
    
    public static final ActionDescriptor ad_create_mp = new ActionDescriptor(ACTION_CREATE_MP);
    public static final ActionDescriptor ad_debugdraw = new ActionDescriptor(ACTION_DEBUG_DRAW);
    public static final ActionDescriptor ad_gc = new ActionDescriptor(ACTION_DEBUG_GC);
    public static final ActionDescriptor ad_join = new ActionDescriptor(ACTION_JOIN);
    public static final ActionDescriptor ad_leave = new ActionDescriptor(ACTION_LEAVE);
    public static final ActionDescriptor ad_minimize = new ActionDescriptor(ACTION_MINIMIZE);
    public static final ActionDescriptor ad_maximize = new ActionDescriptor(ACTION_MAXIMIZE);
    public static final ActionDescriptor ad_unmaximize = new ActionDescriptor(ACTION_UNMAXIMIZE);
    public static final ActionDescriptor ad_quit = new ActionDescriptor(ACTION_QUIT);
    public static final ActionDescriptor ad_redo = new ActionDescriptor(ACTION_REDO);
    public static final ActionDescriptor ad_start = new ActionDescriptor(ACTION_START);
    public static final ActionDescriptor ad_startmp = new ActionDescriptor(ACTION_START_MP);
    public static final ActionDescriptor ad_undo = new ActionDescriptor(ACTION_UNDO);

}
