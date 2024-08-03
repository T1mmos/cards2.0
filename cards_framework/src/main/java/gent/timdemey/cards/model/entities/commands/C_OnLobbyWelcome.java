package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.di.Container;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_OnLobbyWelcome;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.Player;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.utils.Debug;

/**
 * Unicast sent from server to client when the client was accepted, and thus has
 * just entered the server lobby.
 * 
 * @author Tim
 */
public class C_OnLobbyWelcome extends CommandBase
{
    public final UUID clientId;
    public final UUID serverId;
    public final String serverMessage;
    public final List<Player> connected;
    public final UUID lobbyAdminId;
    private final CommandFactory _CommandFactory;

    public C_OnLobbyWelcome(
        Container container, CommandFactory commandFactory, 
        P_OnLobbyWelcome parameters)
    {
        super(container, parameters);
        
        this._CommandFactory = commandFactory;
        
        this.clientId = parameters.clientId;
        this.serverId = parameters.serverId;
        this.serverMessage = parameters.serverMessage;
        this.connected = parameters.connected;
        this.lobbyAdminId = parameters.lobbyAdminId;
    }

    @Override
    public CanExecuteResponse canExecute()
    {
        CheckContext(ContextType.UI);

        return CanExecuteResponse.yes();
    }

    @Override
    public void execute()
    {
        CheckContext(ContextType.UI);

        // safety checks: returned clientId == localId
        if (!_State.getLocalId().equals(clientId))
        {
            throw new IllegalArgumentException(
                    "Server returned a WelcomeClient with a clientId not matching the localId");
        }
        if (!_State.getServerId().equals(serverId))
        {
            throw new IllegalArgumentException(
                    "Server returned a WelcomeClient with a serverId not matching the serverId");
        }

        _State.setServerMessage(serverMessage);
        _State.setLobbyAdminId(lobbyAdminId);
        _State.getPlayers().addAll(connected);      
        _State.setGameState(GameState.Lobby);
        
        run(_CommandFactory.ShowDialog_Lobby());
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("clientId", clientId) + Debug.getKeyValue("serverId", serverId)
                + Debug.getKeyValue("serverMessage", serverMessage) + Debug.listEntity("connected", connected);
    }
}
