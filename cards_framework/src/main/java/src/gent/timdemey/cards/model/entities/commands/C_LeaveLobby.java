package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.game.GameState;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

/**
 * Leave the lobby and as such, fully disconnect from the server. All client state is cleaned.
 * @author Tim
 *
 */
public class C_LeaveLobby extends CommandBase
{
    public C_LeaveLobby()
    {
        
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        if (state.getGameState() == GameState.NotConnected)
        {
            return CanExecuteResponse.no("GameState shouldn't be NotConnected");
        }
        return CanExecuteResponse.yes();
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);
        
        // clean all state
        state.getTcpConnectionPool().closeAllConnections();
        state.setGameState(GameState.NotConnected);
        state.setCardGame(null);
        state.getPlayers().clear();
        state.getServers().clear();
        state.setLobbyAdminId(null);
        state.setServerId(null);
        state.setServerMessage(null);
    }

    @Override
    public String toDebugString()
    {
        return "";
    }

}
