package gent.timdemey.cards.model.entities.commands;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.payload.P_JoinGame;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.entities.game.payload.P_Player;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.netcode.TCP_Connection;
import gent.timdemey.cards.services.INetworkService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.utils.Debug;

/**
 * Unicast sent to the server upon attempting to join a lobby.
 * @author Tim
 *
 */
public class C_EnterLobby extends CommandBase
{
    public final String clientName;
    public final UUID clientId;

    public C_EnterLobby(String clientName, UUID clientId)
    {
        this.clientName = clientName;
        this.clientId = clientId;
    }

    public C_EnterLobby(P_JoinGame pl)
    {
        this.clientName = pl.clientName;
        this.clientId = pl.clientId;
    }

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return state.getCardGame() == null;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        INetworkService netServ = Services.get(INetworkService.class);
        
        if(type == ContextType.UI)
        {
            netServ.send(state.getLocalId(), state.getServerId(), this, state.getTcpConnectionPool());
        }
        else if(type == ContextType.Server)
        {
            TCP_Connection tcpConnection = getSourceTcpConnection();
            Logger.info("Player %s (id %s) joining from %s", clientName, clientId, tcpConnection.getRemote());

            state.getTcpConnectionPool().bindUUID(clientId, tcpConnection);

            if (state.getPlayers().getIds().contains(clientId) && !state.getLobbyAdminId().equals(clientId))
            {
                throw new IllegalStateException("This player already joined the lobby: " + clientId + " ( " + clientName + " )");
            }            
           
            Player player;
            if(!state.getLobbyAdminId().equals(clientId))
            {   
                P_Player pl = new P_Player();
                {
                    pl.id = clientId;
                    pl.name = clientName;
                }
                player = new Player(pl);
                state.getPlayers().add(player);
            }
            else
            {
                player = state.getPlayers().get(clientId);
            }
    
            // send unicast to new client
            {
                CommandBase cmd_answer = new C_OnLobbyWelcome(clientId, state.getServerId(), state.getServerMessage(), state.getRemotePlayers(), state.getLobbyAdminId());
                netServ.send(state.getLocalId(), clientId, cmd_answer, state.getTcpConnectionPool());
            }

            // send update to already connected clients
            List<UUID> updateIds = state.getPlayers().getExceptUUID(clientId);
            if(updateIds.size() > 0)
            {
                CommandBase cmd_update = new C_OnLobbyPlayerJoined(player);
                netServ.broadcast(state.getLocalId(), updateIds, cmd_update, state.getTcpConnectionPool());
            }            
        }
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("clientName", clientName) + Debug.getKeyValue("clientId", clientId);
    }
}
