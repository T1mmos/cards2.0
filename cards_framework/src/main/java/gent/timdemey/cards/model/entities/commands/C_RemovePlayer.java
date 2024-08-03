package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.di.Container;
import java.util.UUID;


import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.C_Disconnect.DisconnectReason;
import gent.timdemey.cards.model.entities.commands.C_OnGameToLobby.GameToLobbyReason;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_RemovePlayer;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.Player;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.INetworkService;
import gent.timdemey.cards.utils.Debug;

public class C_RemovePlayer extends CommandBase
{
    public final UUID playerId;
    private final INetworkService _NetworkService;
    private final Logger _Logger;
    private final CommandFactory _CommandFactory;

    public C_RemovePlayer(
        Container container, INetworkService networkService, CommandFactory commandFactory, Logger logger, 
        P_RemovePlayer parameters)
    {
        super(container, parameters);
        
        this._NetworkService = networkService;
        this._CommandFactory = commandFactory;
        this._Logger = logger;
        
        this.playerId = parameters.playerId;
    }

    @Override
    public CanExecuteResponse canExecute()
    {
        if(_State.getGameState() == GameState.Disconnected)
        {
            return CanExecuteResponse.no("A player cannot leave in the Disconnected state");
        }
        if(!_State.getPlayers().contains(playerId))
        {
            return CanExecuteResponse.no("PlayerId " + playerId + " was not found in the State.Players");
        }
        return CanExecuteResponse.yes();
    }

    @Override
    public void execute()
    {
        Player player_removed = removePlayer(playerId);

        if(_ContextType == ContextType.Server)
        {
            if(_State.getLobbyAdminId().equals(player_removed.id))
            {
                // when the lobby admin leaves, server should stop, so make
                // everyone disconnect gracefully, then stop the server
                C_Disconnect cmd_leavelobby = _CommandFactory.CreateDisconnect(DisconnectReason.LobbyAdminLeft);
                _NetworkService.broadcast(_State.getLocalId(), _State.getRemotePlayerIds(), cmd_leavelobby, _State.getTcpConnectionPool());                

                C_StopServer cmd_stopserver = _CommandFactory.CreateStopServer();
                run(cmd_stopserver);
            }
            else 
            {
                // inform all clients
                _NetworkService.broadcast(_State.getLocalId(), _State.getRemotePlayerIds(), this, _State.getTcpConnectionPool());
                
                if(_State.getGameState() != GameState.Lobby)
                {
                    // while ingame, we force every remaining player back to the lobby
                    C_OnGameToLobby cmd_ongametolobby = _CommandFactory.CreateOnGameToLobby(GameToLobbyReason.PlayerLeft);
                    run(cmd_ongametolobby);
                }
            }
        }
    }

    private Player removePlayer(UUID playerId)
    {
        Player player = _State.getPlayers().remove(playerId);
        _Logger.info("Removed player %s, id=%s", player.getName(), player.id);
        return player;
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("playerId", playerId);
    }

}
