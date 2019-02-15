package gent.timdemey.cards.ui.actions;

public class SolShowActionService extends ActionService {
    @Override
    public boolean canExecuteAction(String id) {
        switch(id)
        {
        default:
            return super.canExecuteAction(id);
        }
    }
    
    @Override
    public void executeAction(String id) { 
        switch(id)
        {
        // case AAction.ACTION_CREATE:
            
            // override if we need to specify parameters in the dialog
            // break;
        
        default:
            super.executeAction(id);
        }
    }
}
