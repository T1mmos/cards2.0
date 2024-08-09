package gent.timdemey.cards.services.contract.descriptors;

import gent.timdemey.cards.model.entities.commands.cfg.P_SaveState;

public class ActionDescriptors
{
    public static final ActionDescriptor SHOWABOUT =                        create("action.game.about");
    public static final ActionDescriptor CREATEMP =                         create("action.game.createmp");
    public static final ActionDescriptor DEBUGDRAW =                        create("action.debug.drawoutlines");
    public static final ActionDescriptor GC =                               create("action.debug.gc");
    public static final ActionDescriptor JOIN =                             create("action.game.join");
    public static final ActionDescriptor LEAVEMP =                          create("action.leavemp");
    public static final ActionDescriptor MINIMIZE =                         create("action.frame.minimize");
    public static final ActionDescriptor MAXIMIZE =                         create("action.frame.maximize");
    public static final ActionDescriptor UNMAXIMIZE =                       create("action.frame.unmaximize");
    public static final ActionDescriptor QUIT =                             create("action.app.quit");
    public static final ActionDescriptor REDO =                             create("action.game.redo");
    public static final PayloadActionDescriptor<P_SaveState> SAVECFG =      payload("action.game.savecfg");
    public static final ActionDescriptor SHOWSETTINGS =                     create("action.game.settings");
    public static final ActionDescriptor SHOWMENU =                         create("action.game.showmenu");
    public static final ActionDescriptor TOGGLEMENUMP =                     create("action.game.togglemenump");
    public static final ActionDescriptor STARTSP =                          create("action.game.start");
    public static final ActionDescriptor STARTMP =                          create("action.game.startmp");
    public static final ActionDescriptor UNDO =                             create("action.game.undo");

    public static ActionDescriptor create(String id)
    {
        return new ActionDescriptor(id);
    }
    
    public static <T> PayloadActionDescriptor<T> payload(String id)
    {
        return new PayloadActionDescriptor<T>(id);
    }
    
    private ActionDescriptors()
    {
    }
}
