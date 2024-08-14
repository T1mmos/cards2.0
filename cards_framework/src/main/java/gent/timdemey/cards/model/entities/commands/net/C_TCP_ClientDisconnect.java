package gent.timdemey.cards.model.entities.commands.net;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.services.context.ContextType;

/**
 * Leave the lobby and as such, fully disconnect from the server. All client
 * state is cleaned.
 * 
 * @author Tim
 *
 */
public class C_TCP_ClientDisconnect extends CommandBase<P_TCP_ClientDisconnect>
{    
    public enum DisconnectReason
    {
        /** Lost connection to the server. */
        ConnectionLost,

        /** Local player launched the disconnect action. */
        LocalPlayerLeft,

        /** The lobby admin has left the server. */
        LobbyAdminLeft,

        /** The local player was kicked from the server */
        Kicked,
    }

    public final DisconnectReason reason;

    public C_TCP_ClientDisconnect(
        Container container,
        P_TCP_ClientDisconnect parameters)
    {
        super(container, parameters);
        
        this.reason = parameters.reason;
    }

    @Override
    public CanExecuteResponse canExecute()
    {
        if (_State.getGameState() == GameState.Disconnected)
        {
            return CanExecuteResponse.no("GameState shouldn't be Disconnected");
        }
        return CanExecuteResponse.yes();
    }

    @Override
    public void execute()
    {
        CheckContext(ContextType.UI);

        // clean all state
        _State.setTcpConnectionPool(null);
        _State.setUdpServiceRequester(null);
        _State.setGameState(GameState.Disconnected);
        _State.setCardGame(null);
        _State.getPlayers().clear();
        _State.getUDPServers().clear();
        _State.setLobbyAdminId(null);
        _State.setServer(null);
        _State.setServerMessage(null);
    }
   
    @Override
    public String toDebugString()
    {
        return "";
    }
}
