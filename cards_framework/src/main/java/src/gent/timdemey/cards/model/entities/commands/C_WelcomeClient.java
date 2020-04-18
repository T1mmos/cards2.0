package gent.timdemey.cards.model.entities.commands;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.payload.P_WelcomeClient;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.entities.game.payload.P_Server;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.utils.Debug;

/**
 * Unicast sent from server to client when the client was accepted, and thus has
 * just joined the server lobby.
 * 
 * @author Tim
 */
public class C_WelcomeClient extends CommandBase
{
    public final UUID clientId;
    public final UUID serverId;
    public final String serverMessage;
    public final List<Player> connected;
    public final UUID lobbyAdminId;

    public C_WelcomeClient(UUID clientId, UUID serverId, String serverMessage, List<Player> connected, UUID lobbyAdminId)
    {
        this.clientId = clientId;
        this.serverId = serverId;
        this.serverMessage = serverMessage;
        this.connected = connected;
        this.lobbyAdminId = lobbyAdminId;
    }

    public C_WelcomeClient(P_WelcomeClient pl)
    {
        super(pl);
        this.clientId = pl.clientId;
        this.serverId = pl.serverId;
        this.serverMessage = pl.serverMessage;
        this.connected = pl.connected;
        this.lobbyAdminId = pl.lobbyAdminId;
    }

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return true;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        CheckNotContext(type, ContextType.Server);

        if(type == ContextType.Client)
        {
            // safety checks: returned clientId == localId
            if(!state.getLocalId().equals(clientId))
            {
                throw new IllegalArgumentException("Server returned a WelcomeClient with a clientId not matching the localId");
            }
            if(!state.getServerId().equals(serverId))
            {
                throw new IllegalArgumentException("Server returned a WelcomeClient with a serverId not matching the serverId");
            }

            updateState(state);

            reschedule(ContextType.UI);
        }
        else if(type == ContextType.UI)
        {
            updateState(state);
            schedule(ContextType.UI, new D_Lobby());
        }
    }

    private void updateState(State state)
    {
        state.setServerMessage(serverMessage);
        state.setLobbyAdminId(lobbyAdminId);
        
        // "connected" enlists all players including yourself
        for (Player player : connected)
        {
            state.getPlayers().add(player);
        }
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("clientId", clientId) + Debug.getKeyValue("serverId", serverId) + Debug.getKeyValue("serverMessage", serverMessage) + Debug
            .listEntity("connected", connected);
    }
}
