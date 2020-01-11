package gent.timdemey.cards.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import gent.timdemey.cards.Services;

class AAction extends AbstractAction {

    public static final String ACTION_QUIT = "action.quit";
    public static final String ACTION_CREATE = "action.create";
    public static final String ACTION_START = "action.start";
    public static final String ACTION_STOP = "action.stop";
    public static final String ACTION_LEAVE = "action.leave";
    public static final String ACTION_JOIN = "action.join";
    public static final String ACTION_DEBUG = "action.debug";
    public static final String ACTION_UNDO = "action.undo";
    public static final String ACTION_REDO = "action.redo";
            
    private final String action;
    
    protected AAction(String action, String title)
    {
        super(title);
        this.action = action;
        checkEnabled();
    }
    
    @Override
    public final void actionPerformed(ActionEvent e)
    {
        Services.get(IActionService.class).executeAction(action);
    }
    
    protected final void checkEnabled()
    {
        setEnabled(Services.get(IActionService.class).canExecuteAction(action));
    }    
}
