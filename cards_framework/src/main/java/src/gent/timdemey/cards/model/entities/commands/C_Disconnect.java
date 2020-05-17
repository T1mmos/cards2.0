package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_Disconnect;
import gent.timdemey.cards.model.entities.game.GameState;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

/**
 * Leave the lobby and as such, fully disconnect from the server. All client state is cleaned.
 * @author Tim
 *
 */
public class C_Disconnect extends CommandBase
{
    public enum DisconnectReason
    {
        ConnectionLost,
        LocalPlayerLeft,
        LobbyAdminLeft,
        Kicked,
    }
    
    public final DisconnectReason reason;
    
    public C_Disconnect(DisconnectReason reason)
    {
        this.reason = reason;
    }
    
    public C_Disconnect(P_Disconnect pl)
    {
        super(pl);
        
        this.reason = pl.reason;
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
    protected void preExecute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);
        
        // clean all state
        state.setTcpConnectionPool(null);
        state.setUdpServiceRequester(null);
        state.setGameState(GameState.NotConnected);
        state.setCardGame(null);
        state.getPlayers().clear();
        state.getUDPServers().clear();
        state.setLobbyAdminId(null);
        state.setServer(null);
        state.setServerMessage(null);
    }

    @Override
    public String toDebugString()
    {
        return "";
    }
}
