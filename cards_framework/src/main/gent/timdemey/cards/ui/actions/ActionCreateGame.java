package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.entities.ContextType;
import gent.timdemey.cards.entities.IContextProvider;
import gent.timdemey.cards.entities.IContextProviderListener;
import gent.timdemey.cards.localization.Loc;

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
        Services.get(IContextProvider.class).addContextListener(new ContextListener());        
    }
}
