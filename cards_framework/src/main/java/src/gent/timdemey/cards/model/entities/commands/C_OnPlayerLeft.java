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
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return true;
    }

    @Override
    public void execute(Context context, ContextType contextType, State state)
    {
        Player player = state.getPlayers().remove(playerId);
        Logger.info("Removed player %s", player.name);
        
        if (contextType == ContextType.UI)
        {
            // if the game is ongoing, inform the user
            // in case of a lobby the UI will be updated without further actions
            if (state.getGameState() == GameState.Started || state.getGameState() == GameState.Paused)
            {
                CommandBase cmd_disconnect = new C_Disconnect();
                schedule(ContextType.UI, cmd_disconnect);
                
                CommandBase cmd_dialog = new D_OnPlayerLeft(player.name);
                schedule(ContextType.UI, cmd_dialog);
            }            
        }
        else
        {
            INetworkService ns = Services.get(INetworkService.class);      
            List<UUID> remotes = state.getRemotePlayerIds();
            if (remotes.size() > 0)
            {
                ns.broadcast(state.getLocalId(), state.getRemotePlayerIds(), this, state.getTcpConnectionPool());    
            }                        
        }
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("playerId", playerId);
    }

}
