package gent.timdemey.cards.localization;

public final class LocKey
{
    /// KEYS

    public static final LocKey Action_about = Action("about");
    public static final LocKey Action_createmp = Action("creategame");  
    public static final LocKey Action_edit = Action("edit");
    public static final LocKey Action_joinmp = Action("join");  
    public static final LocKey Action_leavemp = Action("leave");  
    public static final LocKey Action_maximize = Action("maximize");
    public static final LocKey Action_minimize = Action("minimize");
    public static final LocKey Action_newgame = Action("newgame");  
    public static final LocKey Action_savecfg = Action("savecfg");
    public static final LocKey Action_showmenu = Action("showmenu");
    public static final LocKey Action_showsettings = Action("showsettings");
    public static final LocKey Action_stopgame = Action("stopgame");  
    public static final LocKey Action_quit = Action("quit");    
    public static final LocKey Action_redo = Action("redo");  
    public static final LocKey Action_togglemenump = Action("togglemenump");
    public static final LocKey Action_undo = Action("undo");  
    public static final LocKey Action_unmaximize = Action("unmaximize");

    public static final LocKey Button_cancel = Button("cancel");
    public static final LocKey Button_no = Button("no");
    public static final LocKey Button_ok = Button("ok");
    public static final LocKey Button_refresh = Button("refresh");
    public static final LocKey Button_yes = Button("yes");
    public static final LocKey Button_startmp = Button("startmp");
    
    public static final LocKey CheckBox_autoconnect = CheckBox("autoconnect");
    
    public static final LocKey DebugMenu_debug = DebugMenu("debug");
    public static final LocKey DebugMenu_updateimages = DebugMenu("updateimages");
    public static final LocKey DebugMenu_drawdebuglines = DebugMenu("drawdebuglines");
    public static final LocKey DebugMenu_gc = DebugMenu("gc");

    public static final LocKey Label_serversfound = Label("serversfound");
    public static final LocKey Label_servername = Label("servername");
    public static final LocKey Label_servermsg = Label("servermsg");
    public static final LocKey Label_playername = Label("playername");    
    public static final LocKey Label_waitingToStart = Label("waitingtostart");
    public static final LocKey Label_vs = Label("vs");
    public static final LocKey Label_emptyPlayer = Label("emptyplayer");
    
    public static final LocKey DialogMessage_connectionlost = DialogMessage("connectionlost");
    public static final LocKey DialogMessage_commandundone = DialogMessage("commandundone");
    public static final LocKey DialogMessage_generalerror = DialogMessage("generalerror");
    public static final LocKey DialogMessage_creategame = DialogMessage("creategame");
    public static final LocKey DialogMessage_joingame = DialogMessage("joingame");
    public static final LocKey DialogMessage_kicked = DialogMessage("kicked");
    public static final LocKey DialogMessage_lobby = DialogMessage("lobby");
    public static final LocKey DialogMessage_lobbyAdminLeft = DialogMessage("lobbyAdminLeft");    
    public static final LocKey DialogMessage_lobbyFull = DialogMessage("lobbyFull");    
    public static final LocKey DialogMessage_playerleft = DialogMessage("playerleft");
    public static final LocKey DialogMessage_youlose = DialogMessage("youlose");
    public static final LocKey DialogMessage_youwin = DialogMessage("youwin");
    
    public static final LocKey DialogTitle_commandundone = DialogTitle("commandundone");
    public static final LocKey DialogTitle_connectionlost = DialogTitle("connectionlost");
    public static final LocKey DialogTitle_generalerror = DialogTitle("generalerror");
    public static final LocKey DialogTitle_creategame = DialogTitle("creategame");
    public static final LocKey DialogTitle_joingame = DialogTitle("joingame");
    public static final LocKey DialogTitle_kicked = DialogTitle("kicked");
    public static final LocKey DialogTitle_lobby = DialogTitle("lobby");
    public static final LocKey DialogTitle_lobbyAdminLeft = DialogTitle("lobbyAdminLeft");    
    public static final LocKey DialogTitle_lobbyFull = DialogTitle("lobbyFull");
    public static final LocKey DialogTitle_playerleft = DialogTitle("playerleft");
    public static final LocKey DialogTitle_youlose = DialogTitle("youlose");
    public static final LocKey DialogTitle_youwin = DialogTitle("youwin");
       
    public static final LocKey TableColumnTitle_ipaddress = TableColumnTitle("ipaddress");
    public static final LocKey TableColumnTitle_lobbyPlayerCounts = TableColumnTitle("lobbyPlayerCounts");
    public static final LocKey TableColumnTitle_servername = TableColumnTitle("servername");
        
    //// PREFIXES
    
    private static final String DELIM = ".";
    private static final String PREFIX_BUTTON = "button";
    private static final String PREFIX_CHECKBOX = "checkbox";
    private static final String PREFIX_COLUMN = "column";
    private static final String PREFIX_DEBUG = "debug";
    private static final String PREFIX_DIALOG = "dialog";
    private static final String PREFIX_LABEL = "label";
    private static final String PREFIX_TABLE = "table";
    private static final String PREFIX_TITLE = "title";
    private static final String PREFIX_ACTION = "action";
    private static final String PREFIX_MESSAGE = "message";
    
    // full string key
    final String full;
    
    private LocKey(String full)
    {
        this.full = full;
    }
    
    private static LocKey Button (String str)
    {
        return from(PREFIX_BUTTON, str);
    }
    
    private static LocKey CheckBox (String str)
    {
        return from(PREFIX_CHECKBOX, str);
    }
    
    private static LocKey DialogMessage (String str)
    {
        return from(PREFIX_DIALOG, PREFIX_MESSAGE, str);        
    }
    
    private static LocKey DialogTitle (String str)
    {
        return from(PREFIX_DIALOG, PREFIX_TITLE, str);        
    }
    
    private static LocKey DebugMenu(String str)
    {
        return from(PREFIX_DEBUG, PREFIX_ACTION, str);
    }
    
    private static LocKey Label(String str)
    {
        return from(PREFIX_LABEL, str);
    }
    
    private static LocKey Action(String str)
    {
        return from(PREFIX_ACTION, str);
    }
    
    private static LocKey TableColumnTitle(String str)
    {
        return from(PREFIX_TABLE, PREFIX_COLUMN, PREFIX_TITLE, str);
    }
    
    private static LocKey from(String ... parts)
    {
        String full = String.join(DELIM, parts);
        return new LocKey (full);
    }
}
