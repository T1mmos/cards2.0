package gent.timdemey.cards.ui.actions;

import java.util.List;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.Change;

public class ActionRedo extends AAction
{

    private class RedoListener implements IStateListener
    {

        @Override
        public void onChange(List<Change<?>> changes)
        {
            // TODO Auto-generated method stub
            
        }
        
    }
    
    private final IStateListener redoListener;

    public ActionRedo()
    {
        super(ACTION_REDO, Loc.get("menuitem_redo"));
        this.redoListener = new RedoListener();
        Services.get(IContextService.class).getThreadContext().addStateListener(new RedoStateListener());
        checkEnabled();
    }
}
