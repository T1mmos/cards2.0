package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.utils.Lazy;

public class ActionFactory implements IActionFactory
{
    private Lazy<ActionDef> ad_create = new Lazy<>(() -> new ActionDef(new A_CreateMultiplayerGame(), "C"));
    private Lazy<ActionDef> ad_debug = new Lazy<>(() -> new ActionDef(new A_DebugDrawDebugLines(), "ctrl D"));
    private Lazy<ActionDef> ad_gc = new Lazy<>(() -> new ActionDef(new A_DebugGC(), null));
    private Lazy<ActionDef> ad_join = new Lazy<>(() -> new ActionDef(new A_JoinGame(), "J"));
    private Lazy<ActionDef> ad_leave = new Lazy<>(() -> new ActionDef(new A_LeaveGame(), null));
    private Lazy<ActionDef> ad_quit = new Lazy<>(() -> new ActionDef(new A_QuitGame(), "alt F4"));
    private Lazy<ActionDef> ad_redo = new Lazy<>(() -> new ActionDef(new A_Redo(), "ctrl Y"));
    private Lazy<ActionDef> ad_start = new Lazy<>(() -> new ActionDef(new A_StartGame(), null));
    private Lazy<ActionDef> ad_startmultiplayer = new Lazy<>(() ->  new ActionDef(new A_StartMultiplayerGame(), null));
    private Lazy<ActionDef> ad_stop = new Lazy<>(() -> new ActionDef(new A_StopGame(), null));
    private Lazy<ActionDef> ad_undo = new Lazy<>(() -> new ActionDef(new A_Undo(), "ctrl Z"));

    @Override
    public ActionDef getActionDef(String action)
    {        
        switch (action)
        {
            case Actions.ACTION_CREATE_MULTIPLAYER:
                return ad_create.get();
            case Actions.ACTION_DEBUG:
                return ad_debug.get();
            case Actions.ACTION_GC:
                return ad_gc.get();
            case Actions.ACTION_JOIN:
                return ad_join.get();
            case Actions.ACTION_LEAVE:
                return ad_leave.get();
            case Actions.ACTION_QUIT:
                return ad_quit.get();
            case Actions.ACTION_REDO:
                return ad_redo.get();
            case Actions.ACTION_START:
                return ad_start.get();
            case Actions.ACTION_STARTMULTIPLAYER:
                return ad_startmultiplayer.get();
            case Actions.ACTION_STOP:
                return ad_stop.get();
            case Actions.ACTION_UNDO:
                return ad_undo.get();
            default:
                throw new UnsupportedOperationException("No such action defined: " + action);
        }
    }

}
