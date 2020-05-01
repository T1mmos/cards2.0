package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.game.GameState;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

/**
 * Transition from the multiplayer game to the lobby. Reasons may vary:
 * a player left midgame, the game has ended, the lobby admin id 
 * forcefully ended the game, etc. Game state is cleaned but other state
 * e.g. connected players are not removed.
 * 
 * @author Tim
 */
public class C_OnGameToLobby extends CommandBase
{
    C_OnGameToLobby()
    {
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        if (state.getGameState() != GameState.Started)
        {
            return CanExecuteResponse.no("GameState should be Started but is: " + state.getGameState());
        }
        return CanExecuteResponse.yes();
    }

    @Override
    public void execute(Context context, ContextType type, State state)
    {
        state.setCardGame(null);
        state.setGameState(GameState.Lobby);
        
        if (type == ContextType.UI)
        {
            D_ShowLobby cmd_showlobby = new D_ShowLobby();
            schedule(type, cmd_showlobby);
        }
    }

    @Override
    public String toDebugString()
    {
        return "";
    }
}
