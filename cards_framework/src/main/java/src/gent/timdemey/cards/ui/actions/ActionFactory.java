package gent.timdemey.cards.ui.actions;

public class ActionFactory implements IActionFactory
{
    private ActionDef ad_create = new ActionDef(new A_CreateGame(), "C");
    private ActionDef ad_debug = new ActionDef(new A_DebugDrawDebugLines(), "ctrl D");
    private ActionDef ad_gc = new ActionDef(new A_DebugGC(), null);
    private ActionDef ad_join = new ActionDef(new A_JoinGame(), "J");
    private ActionDef ad_leave = new ActionDef(new A_LeaveGame(), null);
    private ActionDef ad_quit = new ActionDef(new A_QuitGame(), "alt F4");
    private ActionDef ad_redo = new ActionDef(new A_Redo(), "ctrl Y");
    private ActionDef ad_start = new ActionDef(new A_StartGame(), null);
    private ActionDef ad_stop = new ActionDef(new A_StopGame(), null);
    private ActionDef ad_undo = new ActionDef(new A_Undo(), "ctrl Z");

    @Override
    public ActionDef getActionDef(String action)
    {
        switch (action)
        {
            case Actions.ACTION_CREATE:
                return ad_create;
            case Actions.ACTION_DEBUG:
                return ad_debug;
            case Actions.ACTION_GC:
                return ad_gc;
            case Actions.ACTION_JOIN:
                return ad_join;
            case Actions.ACTION_LEAVE:
                return ad_leave;
            case Actions.ACTION_QUIT:
                return ad_quit;
            case Actions.ACTION_REDO:
                return ad_redo;
            case Actions.ACTION_START:
                return ad_start;
            case Actions.ACTION_STOP:
                return ad_stop;
            case Actions.ACTION_UNDO:
                return ad_undo;
            default:
                throw new UnsupportedOperationException("No such action defined: " + action);
        }
    }

}
