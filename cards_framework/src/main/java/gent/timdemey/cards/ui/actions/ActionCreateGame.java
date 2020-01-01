package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.readonlymodel.IContextProviderListener;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.ContextType;

/**
 * Action to start hosting / creating a server.
 * @author Timmos
 *
 */
public class ActionCreateGame extends AAction {
    
    private class ContextListener implements IContextProviderListener
    {
        @Override
        public void onContextAdded(ContextType type) {
            checkEnabled();
        }

        @Override
        public void onContextRemoved(ContextType type) {
            checkEnabled();
        }
    }
        
    public ActionCreateGame() {
        super(AAction.ACTION_CREATE, Loc.get("menuitem_creategame"));
        Services.get(IContextService.class).addContextListener(new ContextListener());        
    }
}
