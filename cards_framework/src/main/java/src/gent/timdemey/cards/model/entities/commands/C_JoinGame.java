package gent.timdemey.cards.model.entities.commands;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.model.entities.commands.payload.P_JoinGame;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.entities.game.payload.P_Player;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.multiplayer.io.TCP_Connection;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.utils.Debug;

public class C_JoinGame extends CommandBase
{
    public final String clientName;
    public final UUID clientId;

    public C_JoinGame(String clientName, UUID clientId)
    {
        this.clientName = clientName;
        this.clientId = clientId;
    }

    public C_JoinGame(P_JoinGame pl)
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
        if(type == ContextType.Client)
        {
            String json = getCommandDtoMapper().toJson(this);
            TCP_Connection tcpConnection = state.getTcpConnectionPool().getConnection(state.getServerId());
            tcpConnection.send(json);
        }
        else if(type == ContextType.Server)
        {
            TCP_Connection tcpConnection = getSourceTcpConnection();
            Services.get(ILogManager.class).log("Player %s (id %s) joining from %s", clientName, clientId, tcpConnection.getRemote());

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
                CommandBase cmd_answer = new C_WelcomeClient(clientId, state.getServerId(), state.getServerMessage(), state.getRemotePlayers(), state.getLobbyAdminId());
                String json_answer = getCommandDtoMapper().toJson(cmd_answer);
                state.getTcpConnectionPool().getConnection(clientId).send(json_answer);
            }

            // send update to already connected clients
            List<UUID> updateIds = state.getPlayers().getExceptUUID(clientId);
            if(updateIds.size() > 0)
            {
                CommandBase cmd_update = new C_OnPlayerJoined(player);
                String json_update = getCommandDtoMapper().toJson(cmd_update);
                state.getTcpConnectionPool().broadcast(updateIds, json_update);
            }
            
            
        }
        else
        {
            throw new IllegalStateException();
        }
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("clientName", clientName) + Debug.getKeyValue("clientId", clientId);
    }
}
