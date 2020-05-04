package gent.timdemey.cards.ui.actions;

import javax.swing.SwingUtilities;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.context.IContextListener;

/**
 * Action to start hosting / creating a server.
 * 
 * @author Timmos
 *
 */
public class A_CreateMultiplayerGame extends ActionBase
{
    private class ContextListener implements IContextListener
    {
        @Override
        public void onContextInitialized(ContextType type)
        {
            onContextChange(type);
        }

        @Override
        public void onContextDropped(ContextType type)
        {
            onContextChange(type);
        }
        
        private void onContextChange(ContextType type)
        {
            if (type == ContextType.Server)
            {
                // the listener can be invoked from different contexts, 
                // but we want to ensure being on the UI context
                SwingUtilities.invokeLater(() -> checkEnabled());
            }
        }
    }
    
    protected A_CreateMultiplayerGame()
    {
        super(Actions.ACTION_CREATE_MULTIPLAYER, Loc.get(LocKey.Menu_creategame));
        Services.get(IContextService.class).addContextListener(new ContextListener());
        checkEnabled();
    }
}
