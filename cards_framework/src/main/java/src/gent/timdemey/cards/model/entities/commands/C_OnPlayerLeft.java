package gent.timdemey.cards.model.entities.commands;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.payload.P_OnPlayerLeft;
import gent.timdemey.cards.model.entities.game.GameState;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.INetworkService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.utils.Debug;

public class C_OnPlayerLeft extends CommandBase
{
    public final UUID playerId;

    public C_OnPlayerLeft(UUID playerId)
    {
        this.playerId = playerId;
    }

    public C_OnPlayerLeft(P_OnPlayerLeft pl)
    {
        super(pl);
        this.playerId = pl.playerId;
    }
    
    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        return state.getPlayers().contains(playerId);
    }

    @Override
    public void execute(Context context, ContextType contextType, State state)
    {
        Player player_removed = removePlayer(state, playerId);
        
        if (contextType == ContextType.UI)
        {
            if (state.getGameState() == GameState.Started || state.getGameState() == GameState.Paused)
            {         
                // inform the user
                CommandBase cmd_dialog = new D_OnPlayerLeft(player_removed.name);
                run(cmd_dialog);
                
                // if the game is ongoing then the entire game ends
                C_OnGameToLobby cmd_ongametolobby = new C_OnGameToLobby();
                schedule(ContextType.UI, cmd_ongametolobby);     
            }
        }
        else
        {
            C_OnGameToLobby cmd_ongametolobby = new C_OnGameToLobby();
            schedule(ContextType.Server, cmd_ongametolobby);   
            
            INetworkService ns = Services.get(INetworkService.class);      
            List<UUID> remotes = state.getRemotePlayerIds();
            if (remotes.size() > 0)
            {
                ns.broadcast(state.getLocalId(), state.getRemotePlayerIds(), this, state.getTcpConnectionPool());    
            }                        
        }
    }
    
    private Player removePlayer(State state, UUID playerId)
    {
        Player player = state.getPlayers().remove(playerId);
        Logger.info("Removed player %s", player.name);
        return player;
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("playerId", playerId);
    }

}
