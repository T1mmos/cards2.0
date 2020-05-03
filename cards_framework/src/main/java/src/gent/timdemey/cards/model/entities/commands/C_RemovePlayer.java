package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.C_Disconnect.DisconnectReason;
import gent.timdemey.cards.model.entities.commands.C_OnGameToLobby.GameToLobbyReason;
import gent.timdemey.cards.model.entities.commands.payload.P_RemovePlayer;
import gent.timdemey.cards.model.entities.game.GameState;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.INetworkService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.utils.Debug;

public class C_RemovePlayer extends CommandBase
{
    public final UUID playerId;

    public C_RemovePlayer(UUID playerId)
    {
        this.playerId = playerId;
    }

    public C_RemovePlayer(P_RemovePlayer pl)
    {
        super(pl);
        this.playerId = pl.playerId;
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        if(state.getGameState() == GameState.NotConnected)
        {
            return CanExecuteResponse.no("A player cannot leave if the NotConnected state");
        }
        if(!state.getPlayers().contains(playerId))
        {
            return CanExecuteResponse.no("PlayerId " + playerId + " was not found in the State.Players");
        }
        return CanExecuteResponse.yes();
    }

    @Override
    public void execute(Context context, ContextType contextType, State state)
    {
        Player player_removed = removePlayer(state, playerId);

        if(contextType == ContextType.Server)
        {
            INetworkService ns = Services.get(INetworkService.class);

            if(state.getLobbyAdminId().equals(player_removed.id))
            {
                // when the lobby admin leaves, server should stop, so make
                // everyone disconnect gracefully, then stop the server
                C_Disconnect cmd_leavelobby = new C_Disconnect(DisconnectReason.LobbyAdminLeft);
                ns.broadcast(state.getLocalId(), state.getRemotePlayerIds(), cmd_leavelobby, state.getTcpConnectionPool());                

                C_StopServer cmd_stopserver = new C_StopServer();
                run(cmd_stopserver);
            }
            else 
            {
                // inform all clients
                ns.broadcast(state.getLocalId(), state.getRemotePlayerIds(), this, state.getTcpConnectionPool());
                
                if(state.getGameState() != GameState.Lobby)
                {
                    // while ingame, we force every remaining player back to the lobby
                    C_OnGameToLobby cmd_ongametolobby = new C_OnGameToLobby(GameToLobbyReason.PlayerLeft);
                    run(cmd_ongametolobby);
                }
            }
        }
    }

    private Player removePlayer(State state, UUID playerId)
    {
        Player player = state.getPlayers().remove(playerId);
        Logger.info("Removed player %s, id=%s", player.name, player.id);
        return player;
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("playerId", playerId);
    }

}
