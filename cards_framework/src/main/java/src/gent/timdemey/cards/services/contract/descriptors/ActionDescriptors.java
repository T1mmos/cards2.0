package gent.timdemey.cards.services.contract.descriptors;

public class ActionDescriptors
{
    public static final String ACTION_DEBUG_DRAW = "action.debug.drawoutlines";
    public static final String ACTION_DEBUG_GC = "action.debug.gc";
    public static final String ACTION_CREATE_MP = "action.create";
    public static final String ACTION_JOIN = "action.join";
    public static final String ACTION_LEAVE = "action.leave";
    public static final String ACTION_MINIMIZE = "action.minimize";
    public static final String ACTION_MAXIMIZE = "action.maximize";
    public static final String ACTION_UNMAXIMIZE = "action.unmaximize";
    public static final String ACTION_QUIT = "action.quit";
    public static final String ACTION_REDO = "action.redo";
    public static final String ACTION_START = "action.start";
    public static final String ACTION_START_MP = "action.startmultiplayer";
    public static final String ACTION_UNDO = "action.undo";
    
    public static final ActionDescriptor ad_create_mp = new ActionDescriptor(ACTION_CREATE_MP, "C");
    public static final ActionDescriptor ad_debugdraw = new ActionDescriptor(ACTION_DEBUG_DRAW, "ctrl D");
    public static final ActionDescriptor ad_gc = new ActionDescriptor(ACTION_DEBUG_GC, null);
    public static final ActionDescriptor ad_join = new ActionDescriptor(ACTION_JOIN, "J");
    public static final ActionDescriptor ad_leave = new ActionDescriptor(ACTION_LEAVE, null);
    public static final ActionDescriptor ad_minimize = new ActionDescriptor(ACTION_MINIMIZE, "alt F2");
    public static final ActionDescriptor ad_maximize = new ActionDescriptor(ACTION_MAXIMIZE, "alt F3");
    public static final ActionDescriptor ad_unmaximize = new ActionDescriptor(ACTION_UNMAXIMIZE, "alt F3");
    public static final ActionDescriptor ad_quit = new ActionDescriptor(ACTION_QUIT, "alt F4");
    public static final ActionDescriptor ad_redo = new ActionDescriptor(ACTION_REDO, "ctrl Y");
    public static final ActionDescriptor ad_start = new ActionDescriptor(ACTION_START, null);
    public static final ActionDescriptor ad_startmp = new ActionDescriptor(ACTION_START_MP, null);
    public static final ActionDescriptor ad_undo = new ActionDescriptor(ACTION_UNDO, "ctrl Z");

}
