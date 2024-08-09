package gent.timdemey.cards.model.entities.commands.lobby;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CommandFactory;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;
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
public class C_OnWelcome extends CommandBase<P_OnWelcome>
{
    public final UUID clientId;
    public final UUID serverId;
    public final String serverMessage;
    public final List<Player> connected;
    public final UUID lobbyAdminId;
    private final CommandFactory _CommandFactory;

    public C_OnWelcome(
        Container container, CommandFactory commandFactory, 
        P_OnWelcome parameters)
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
        if (!_State.getServer().id.equals(serverId))
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
