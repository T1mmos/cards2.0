package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.IContextService;

/**
 * Action to start hosting / creating a server.
 * 
 * @author Timmos
 *
 */
public class A_CreateMultiplayerGame extends ActionBase
{
    private class GameStateListener implements IStateListener
    {

        @Override
        public void onChange(ReadOnlyChange change)
        {
            if (change.property == ReadOnlyState.GameState)
            {
                checkEnabled();
            }
        }
        
    }
    
    protected A_CreateMultiplayerGame()
    {
        super(Actions.ACTION_CREATE_MULTIPLAYER, Loc.get(LocKey.Menu_creategame));
        Services.get(IContextService.class).getThreadContext().addStateListener(new GameStateListener());
        checkEnabled();
    }
}
