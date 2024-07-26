package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;


import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.C_Disconnect.DisconnectReason;
import gent.timdemey.cards.model.entities.commands.C_OnGameToLobby.GameToLobbyReason;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.Player;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.INetworkService;
import gent.timdemey.cards.utils.Debug;

public class C_RemovePlayer extends CommandBase
{
    public final UUID playerId;
    private final INetworkService _NetworkService;
    private final Logger _Logger;
    private final CommandFactory _CommandFactory;

    C_RemovePlayer(
        IContextService contextService, INetworkService networkService, CommandFactory commandFactory, Logger logger, 
        UUID id, UUID playerId)
    {
        super(contextService, id);
        
        this._NetworkService = networkService;
        this._CommandFactory = commandFactory;
        this._Logger = logger;
        
        this.playerId = playerId;
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        if(state.getGameState() == GameState.Disconnected)
        {
            return CanExecuteResponse.no("A player cannot leave in the Disconnected state");
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
            if(state.getLobbyAdminId().equals(player_removed.id))
            {
                // when the lobby admin leaves, server should stop, so make
                // everyone disconnect gracefully, then stop the server
                C_Disconnect cmd_leavelobby = _CommandFactory.CreateDisconnect(DisconnectReason.LobbyAdminLeft);
                _NetworkService.broadcast(state.getLocalId(), state.getRemotePlayerIds(), cmd_leavelobby, state.getTcpConnectionPool());                

                C_StopServer cmd_stopserver = _CommandFactory.CreateStopServer();
                run(cmd_stopserver);
            }
            else 
            {
                // inform all clients
                _NetworkService.broadcast(state.getLocalId(), state.getRemotePlayerIds(), this, state.getTcpConnectionPool());
                
                if(state.getGameState() != GameState.Lobby)
                {
                    // while ingame, we force every remaining player back to the lobby
                    C_OnGameToLobby cmd_ongametolobby = _CommandFactory.CreateOnGameToLobby(GameToLobbyReason.PlayerLeft);
                    run(cmd_ongametolobby);
                }
            }
        }
    }

    private Player removePlayer(State state, UUID playerId)
    {
        Player player = state.getPlayers().remove(playerId);
        _Logger.info("Removed player %s, id=%s", player.getName(), player.id);
        return player;
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("playerId", playerId);
    }

}
